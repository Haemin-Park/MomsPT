package com.fitsionary.momspt.util

import com.fitsionary.momspt.data.api.response.Landmark
import com.fitsionary.momspt.domain.WorkoutLandmarkDomainModel
import timber.log.Timber
import kotlin.math.abs

class ScoringAlgorithm(workoutLandmarks: WorkoutLandmarkDomainModel) {
    val landmarksList = workoutLandmarks.poseData
    private var count = 0
    var targetKeyPoints = ArrayList<Double>()

    private val DTWTarget = ArrayList<ArrayList<Double>>()
    private val DTWResource = ArrayList<ArrayList<Double>>()

    /**
     * 프레임 당 키포인트 추출 후 호출하는 함수
     * FRAME_CUT만큼 잘라서 DTW를 적용하고 결과값 반환
     */
    fun pushKeyPoints(
        keyPoints: ArrayList<Landmark>,
        cameraWidth: Int,
        cameraHeight: Int
    ): Double {
        targetKeyPoints = keyPoints2Vec(keyPoints, cameraWidth, cameraHeight)
        var result: Double
        DTWTarget.add(targetKeyPoints)
        count++
        if (count % FRAME_CUT == 0) {
            // target 과 비교할 프레임만큼 resource 만들기
            for (i in count - FRAME_CUT until count) {

                val resourceKeypoints = keyPoints2Vec(landmarksList[i].landmarks, 720, 404)
                DTWResource.add(resourceKeypoints)
            }
            result = DTW(DTWTarget, DTWResource)
            DTWTarget.clear()
            DTWResource.clear()
            result = -1 * 500 / FRAME_CUT * result + 100
            return if (result > 0) result else 0.0
        }
        return (-1).toDouble()
    }

    /**
     * 키 포인트 벡터화
     */
    private fun keyPoints2Vec(
        keyPoints: List<Landmark>,
        cameraWidth: Int,
        cameraHeight: Int
    ): ArrayList<Double> {
        var vector = ArrayList<Double>()
        var minX = Double.POSITIVE_INFINITY
        var minY = Double.POSITIVE_INFINITY
        var scaler = Double.NEGATIVE_INFINITY

        // 1. 벡터 배열로 만들기, scale 을 위한 변수 계산하기.
        for (i in keyPoints.indices) {
            val xValue: Double = keyPoints[i].x * cameraWidth
            val yValue: Double = keyPoints[i].y * cameraHeight
            vector.add(xValue)
            vector.add(yValue)
            minX = Math.min(minX, xValue)
            minY = Math.min(minY, yValue)
            scaler = Math.max(scaler, Math.max(xValue, yValue))
        }

        // 2. scale & translate
        vector = scaleAndTranslate(vector, minX / scaler, minY / scaler, scaler)

        // L2 normalization
        vector = l2Normalize(vector)
        return vector
    }

    private fun scaleAndTranslate(
        vector: ArrayList<Double>,
        transX: Double,
        transY: Double,
        scaler: Double
    ): ArrayList<Double> {
        for (i in 0 until vector.size) {
            if (i % 2 == 0) {
                vector[i] = vector[i] / scaler - transX
            } else {
                vector[i] = vector[i] / scaler - transY
            }
        }
        return vector
    }

    private fun l2Normalize(vector: ArrayList<Double>): ArrayList<Double> {
        var norm = 0.0
        for (x in vector) {
            norm += x * x
        }
        norm = Math.sqrt(norm)
        for (i in 0 until vector.size) {
            vector[i] = vector[i] / norm
        }
        return vector
    }

    /**
     * pose data를 FRAME_CUT씩 잘라서 DTW적용
     * 키포인트 17개의 데이터를 하나의 vector로 표현 후 ArrayList<Double>에 저장
     */
    private fun DTW(
        resourcePoseSet: ArrayList<ArrayList<Double>>,
        targetPoseSet: ArrayList<ArrayList<Double>>
    ): Double {
        val DTW = Array(resourcePoseSet.size) {
            DoubleArray(
                targetPoseSet.size
            )
        }

        // DTW table 초기화
        for (i in 0 until resourcePoseSet.size) {
            for (j in 0 until targetPoseSet.size) {
                if (i == 0 && j == 0) DTW[i][j] = 0.0 else DTW[i][j] = Double.POSITIVE_INFINITY
            }
        }

        // DTW table first row and column 계산
        for (i in 1 until resourcePoseSet.size) {
            DTW[i][0] = DTW[i - 1][0] + cosineDistanceOfPose(resourcePoseSet[i], targetPoseSet[0])
        }
        for (j in 1 until targetPoseSet.size) {
            DTW[0][j] = DTW[0][j - 1] + cosineDistanceOfPose(resourcePoseSet[0], targetPoseSet[j])
        }
        for (i in 1 until resourcePoseSet.size) {
            for (j in 1 until targetPoseSet.size) {
                val cost = cosineDistanceOfPose(resourcePoseSet[i], targetPoseSet[j])
                Timber.i("cost [$i][$j] : $cost")
                DTW[i][j] = cost + Math.min(
                    DTW[i - 1][j - 1], Math.min(
                        DTW[i - 1][j],
                        DTW[i][j - 1]
                    )
                )
                Timber.i(
                    "DTW[" + i.toString() + "][" + j.toString() + "] : " + DTW[i][j].toString()
                )

            }
        }

        return DTW[resourcePoseSet.size - 1][targetPoseSet.size - 1]
    }

    private fun cosineDistanceOfPose(
        poseVecA: ArrayList<Double>,
        poseVecB: ArrayList<Double>
    ): Double {
        var v1Dotv2 = 0.0
        var absV1 = 0.0
        var absV2 = 0.0
        Timber.i("스코어 " + poseVecA.size.toString() + " " + poseVecB.size.toString())
        for (i in 0 until poseVecA.size) {
            val valueA = poseVecA[i]
            val valueB = poseVecB[i]
            v1Dotv2 += valueA * valueB
            absV1 += valueA * valueA
            absV2 += valueB * valueB
        }
        absV1 = Math.sqrt(absV1)
        absV2 = Math.sqrt(absV2)

        val check = v1Dotv2 / (absV1 * absV2)
        return abs(1 - check)
    }

    companion object {
        private const val FRAME_CUT = 10
    }
}