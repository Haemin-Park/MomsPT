package com.fitsionary.momspt.util

import android.util.Log
import com.fitsionary.momspt.data.Landmark

class ScoringAlgorithm(val landmarksList: ArrayList<ArrayList<Landmark>>) {

    private var count = 0
    private val FRAME_CUT = 10

    private val dtwTarget = ArrayList<ArrayList<Double>>()
    private val dtwResource = ArrayList<ArrayList<Double>>()

    /*
    * 프레임 당 Keypoints 추출한 뒤 호출하는 코드이다.
    * Android에서는 프레임마다 pushKeyPoints를 출력하면 된다.
    * FRAME_CUT 만큼 잘라서 dtw를 적용하고 결과값을 낸다.
    */
    fun pushKeyPoints(
        keyPoints: ArrayList<Landmark>,
        cameraWidth: Int,
        cameraHeight: Int
    ): Double {
        Log.i(TAG, "PUSH KEY POINTS!!")
        val targetKeyPoints = keyPoints2Vec(keyPoints, cameraWidth, cameraHeight)
        var result = 0.0
        dtwTarget.add(targetKeyPoints)
        count++
        if (count % FRAME_CUT == 0) {
            //target과 비교할 프레임만큼 Resource도 만들어놓기
            for (i in count - FRAME_CUT until count) {
                val resourceKeypoints = keyPoints2Vec(landmarksList[i], 480, 480)
                dtwResource.add(resourceKeypoints)
            }
            result = dtw(dtwTarget, dtwResource)
            dtwTarget.clear()
            dtwResource.clear()
            return 1 / result * 10
        }
        return (-1).toDouble()
    }

    /*
    * 키 포인트 벡터화
    */
    private fun keyPoints2Vec(
        keyPoints: ArrayList<Landmark>,
        cameraWidth: Int,
        cameraHeight: Int
    ): ArrayList<Double> {
        var vector = ArrayList<Double>()
        var minX = Double.POSITIVE_INFINITY
        var minY = Double.POSITIVE_INFINITY
        var scaler = Double.NEGATIVE_INFINITY

        //1. 벡터 배열로 만들기, scale을 위한 변수 계산하기.
        for (i in 0 until keyPoints.size) {
            val xValue: Double = keyPoints[i].x * cameraWidth
            val yValue: Double = keyPoints[i].y * cameraHeight
            vector.add(xValue)
            vector.add(yValue)
            minX = Math.min(minX, xValue)
            minY = Math.min(minY, yValue)
            scaler = Math.max(scaler, Math.max(xValue, yValue))
        }

        //2. scale & translate
        vector = scaleAndTranslate(vector, minX / scaler, minY / scaler, scaler)

        //3. L2 normalization
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
            //vector.set(i, vector.get(i)/norm)
        }
//        for (x in vector) {
//            x = x / norm
//        }
        return vector
    }

    /*
    * dtw 알고리즘을 적용하는 코드이다. pose data를 10프레임씩 잘라서 dtw를 적용한다.
    * 키포인트 17개의 데이터를 -> 하나의 vector로 표현. 그것을 ArrayList<Double>에 저장.
    */
    private fun dtw(
        resourcePoseSet: ArrayList<ArrayList<Double>>,
        targetPoseSet: ArrayList<ArrayList<Double>>
    ): Double {
        val dtw = Array(resourcePoseSet.size) {
            DoubleArray(
                targetPoseSet.size
            )
        }

        // dtw table 초기화
        for (i in 0 until resourcePoseSet.size) {
            for (j in 0 until targetPoseSet.size) {
                if (i == 0 && j == 0) dtw[i][j] = 0.0 else dtw[i][j] = Double.POSITIVE_INFINITY
            }
        }

        // dtw table first row and column 계산
        for (i in 1 until resourcePoseSet.size) {
            dtw[i][0] = dtw[i - 1][0] + consineDistanceOfPose(resourcePoseSet[i], targetPoseSet[0])
        }
        for (j in 1 until targetPoseSet.size) {
            dtw[0][j] = dtw[0][j - 1] + consineDistanceOfPose(resourcePoseSet[0], targetPoseSet[j])
        }
        for (i in 1 until resourcePoseSet.size) {
            for (j in 1 until targetPoseSet.size) {
                val cost = consineDistanceOfPose(resourcePoseSet[i], targetPoseSet[j])
                Log.i(TAG, "cost [$i][$j] : $cost")
                dtw[i][j] = cost + Math.min(
                    dtw[i - 1][j - 1], Math.min(
                        dtw[i - 1][j],
                        dtw[i][j - 1]
                    )
                )
                Log.i(
                    TAG,
                    "dtw[" + i.toString() + "][" + j.toString() + "] : " + dtw[i][j].toString()
                )

            }
        }
        //Dynamic time wrapping 구현.
        return dtw[resourcePoseSet.size - 1][targetPoseSet.size - 1]
    }

    private fun consineDistanceOfPose(
        poseVecA: ArrayList<Double>,
        poseVecB: ArrayList<Double>
    ): Double {
        var v1Dotv2 = 0.0
        var absV1 = 0.0
        var absV2 = 0.0
        for (i in 0 until poseVecA.size) {
            //Log.i(TAG, poseVecA.size.toString() + poseVecB.size.toString())
            val valueA = poseVecA[i]
            val valueB = poseVecB[i]
            v1Dotv2 += valueA * valueB
            absV1 += valueA * valueA
            absV2 += valueB * valueB
        }
        absV1 = Math.sqrt(absV1)
        absV2 = Math.sqrt(absV2)
        return 1 - v1Dotv2 / (absV1 * absV2)
    }

    companion object {
        private val TAG = ScoringAlgorithm::class.java.simpleName
    }
}