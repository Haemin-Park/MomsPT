package com.fitsionary.momspt.util

import com.fitsionary.momspt.data.api.response.Landmark
import com.fitsionary.momspt.data.enum.AngleEnum
import com.fitsionary.momspt.data.enum.WorkoutPoseEnum
import com.fitsionary.momspt.domain.WorkoutLandmarkDomainModel
import timber.log.Timber
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.sqrt


class WorkoutAnalysisAlgorithm(workoutLandmarks: WorkoutLandmarkDomainModel) {
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
        cameraHeight: Int,
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
        scalar: Double
    ): ArrayList<Double> {
        for (i in 0 until vector.size) {
            if (i % 2 == 0) {
                vector[i] = vector[i] / scalar - transX
            } else {
                vector[i] = vector[i] / scalar - transY
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
     * 키포인트 19개의 데이터를 하나의 vector로 표현 후 ArrayList<Double>에 저장
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

    /**
     * 운동 카운팅
     */

    fun calculateAngles(
        keyPoints: ArrayList<Landmark>
    ): Map<String, Double> {
        val leftElbowAngle = getAngleThreePoint(
            keyPoints,
            WorkoutPoseEnum.LEFT_SHOULDER.ordinal,
            WorkoutPoseEnum.LEFT_ELBOW.ordinal,
            WorkoutPoseEnum.LEFT_WRIST.ordinal
        )
        val rightElbowAngle = getAngleThreePoint(
            keyPoints,
            WorkoutPoseEnum.RIGHT_SHOULDER.ordinal,
            WorkoutPoseEnum.RIGHT_ELBOW.ordinal,
            WorkoutPoseEnum.RIGHT_WRIST.ordinal
        )
        val leftShoulderAngle = getAngleThreePoint(
            keyPoints,
            WorkoutPoseEnum.RIGHT_SHOULDER.ordinal,
            WorkoutPoseEnum.LEFT_SHOULDER.ordinal,
            WorkoutPoseEnum.LEFT_ELBOW.ordinal
        )
        val rightShoulderAngle = getAngleThreePoint(
            keyPoints,
            WorkoutPoseEnum.LEFT_SHOULDER.ordinal,
            WorkoutPoseEnum.RIGHT_SHOULDER.ordinal,
            WorkoutPoseEnum.RIGHT_ELBOW.ordinal
        )
        val leftHip2ElbowAngle = getAngleThreePoint(
            keyPoints,
            WorkoutPoseEnum.LEFT_HIP.ordinal,
            WorkoutPoseEnum.LEFT_SHOULDER.ordinal,
            WorkoutPoseEnum.LEFT_ELBOW.ordinal
        )
        val rightHip2ElbowAngle = getAngleThreePoint(
            keyPoints,
            WorkoutPoseEnum.RIGHT_HIP.ordinal,
            WorkoutPoseEnum.RIGHT_SHOULDER.ordinal,
            WorkoutPoseEnum.RIGHT_ELBOW.ordinal
        )
        val leftHipAngle = getAngleThreePoint(
            keyPoints,
            WorkoutPoseEnum.LEFT_SHOULDER.ordinal,
            WorkoutPoseEnum.LEFT_HIP.ordinal,
            WorkoutPoseEnum.LEFT_KNEE.ordinal
        )
        val rightHipAngle = getAngleThreePoint(
            keyPoints,
            WorkoutPoseEnum.RIGHT_SHOULDER.ordinal,
            WorkoutPoseEnum.RIGHT_HIP.ordinal,
            WorkoutPoseEnum.RIGHT_KNEE.ordinal
        )
        val leftHip2KneeAngle = getAngleThreePoint(
            keyPoints,
            WorkoutPoseEnum.RIGHT_HIP.ordinal,
            WorkoutPoseEnum.LEFT_HIP.ordinal,
            WorkoutPoseEnum.LEFT_KNEE.ordinal
        )
        val rightHip2KneeAngle = getAngleThreePoint(
            keyPoints,
            WorkoutPoseEnum.LEFT_HIP.ordinal,
            WorkoutPoseEnum.RIGHT_HIP.ordinal,
            WorkoutPoseEnum.RIGHT_KNEE.ordinal
        )
        val leftKneeAngle = getAngleThreePoint(
            keyPoints,
            WorkoutPoseEnum.LEFT_HIP.ordinal,
            WorkoutPoseEnum.LEFT_KNEE.ordinal,
            WorkoutPoseEnum.LEFT_ANKLE.ordinal
        )
        val rightKneeAngle = getAngleThreePoint(
            keyPoints,
            WorkoutPoseEnum.RIGHT_HIP.ordinal,
            WorkoutPoseEnum.RIGHT_KNEE.ordinal,
            WorkoutPoseEnum.RIGHT_ANKLE.ordinal
        )
        val leftAnkleAngle = getAngleThreePoint(
            keyPoints,
            WorkoutPoseEnum.LEFT_KNEE.ordinal,
            WorkoutPoseEnum.LEFT_ANKLE.ordinal,
            WorkoutPoseEnum.LEFT_FOOT_INDEX.ordinal
        )
        val rightAnkleAngle = getAngleThreePoint(
            keyPoints,
            WorkoutPoseEnum.RIGHT_KNEE.ordinal,
            WorkoutPoseEnum.RIGHT_ANKLE.ordinal,
            WorkoutPoseEnum.RIGHT_FOOT_INDEX.ordinal
        )

        return mapOf(
            AngleEnum.leftElbowAngle.name to leftElbowAngle,
            AngleEnum.rightElbowAngle.name to rightElbowAngle,
            AngleEnum.leftShoulderAngle.name to leftShoulderAngle,
            AngleEnum.rightShoulderAngle.name to rightShoulderAngle,
            AngleEnum.leftHip2ElbowAngle.name to leftHip2ElbowAngle,
            AngleEnum.rightHip2ElbowAngle.name to rightHip2ElbowAngle,
            AngleEnum.leftHipAngle.name to leftHipAngle,
            AngleEnum.rightHipAngle.name to rightHipAngle,
            AngleEnum.leftHip2KneeAngle.name to leftHip2KneeAngle,
            AngleEnum.rightHip2KneeAngle.name to rightHip2KneeAngle,
            AngleEnum.leftKneeAngle.name to leftKneeAngle,
            AngleEnum.rightKneeAngle.name to rightKneeAngle,
            AngleEnum.leftAnkleAngle.name to leftAnkleAngle,
            AngleEnum.rightAnkleAngle.name to rightAnkleAngle
        )
    }

    data class WorkoutState(
        val angle: Double?,
        val up: Boolean,
        val down: Boolean,
        val reset: Boolean,
        val count: Int
    )

    data class CountState(
        val up: Boolean,
        val down: Boolean,
        val reset: Boolean,
        val count: Int
    )

    fun workoutSelector(
        angles: Map<String, Double>,
        workoutCode: String,
        up: Boolean,
        down: Boolean,
        reset: Boolean,
        count: Int
    ): WorkoutState {
        var angle: Double? = 0.0
        var cState = CountState(up, down, reset, count)
        when (workoutCode) {
            "W006" -> {
                angle = angles[AngleEnum.leftAnkleAngle.name]
                cState = countWorkout(angle, 150, 110, up, down, reset, count, 1)
            }
            "W007" -> {
                angle = angles[AngleEnum.leftHipAngle.name]
                cState = countWorkout(angle, 160, 120, up, down, reset, count, 1)
            }
            "W008" -> {
                angle = angles[AngleEnum.rightShoulderAngle.name]
                cState = countWorkout(angle, 160, 90, up, down, reset, count, 1)
            }
            "W009" -> {
                val angle2: Double? = angles[AngleEnum.leftHip2ElbowAngle.name]
                angle = angles[AngleEnum.rightHip2ElbowAngle.name]

                cState = countWorkout(abs(angle!! - angle2!!), 100, 30, up, down, reset, count, 1)
                Timber.i("LOG ANGLE " + abs(angle!! - angle2!!).toString())
            }
            "W011" -> {
                angle = angles[AngleEnum.leftHipAngle.name]
                cState = countWorkout(angle, 110, 95, up, down, reset, count, 0)
            }
            "W012" -> {
                angle = angles[AngleEnum.leftHipAngle.name]
                cState = countWorkout(angle, 110, 90, up, down, reset, count, 0)
            }
            "W015" -> {
                angle = angles[AngleEnum.leftHipAngle.name]
                cState = countWorkout(angle, 150, 120, up, down, reset, count, 0)
            }
            "W020" -> {
                angle = angles[AngleEnum.rightHipAngle.name]
                cState = countWorkout(angle, 170, 120, up, down, reset, count, 0)
            }
            "W022" -> {
                angle = angles[AngleEnum.leftHipAngle.name]
                cState = countWorkout(angle, 160, 110, up, down, reset, count, 0)
            }
            "W023" -> {
                angle = angles[AngleEnum.leftKneeAngle.name]
                cState = countWorkout(angle, 170, 140, up, down, reset, count, 0)
            }
            "W024" -> {
                angle = angles[AngleEnum.leftKneeAngle.name]
                cState = countWorkout(angle, 170, 150, up, down, reset, count, 0)
            }
        }

        return WorkoutState(angle, cState.up, cState.down, cState.reset, cState.count)
    }

    private fun countWorkout(
        angle: Double?,
        upper: Int,
        lower: Int,
        up: Boolean,
        down: Boolean,
        reset: Boolean,
        count: Int,
        mode: Int
    ): CountState {
        var returnup: Boolean = up
        var returndown: Boolean = down
        var returnreset: Boolean = reset
        var returncount: Int = count
        if (mode == 0) {
            if (angle!! > upper && !up) {
                returnup = true
            } else if (angle < lower && !down) {
                returndown = true
            } else if (angle > upper && up && down) {
                returncount = count + 1
                returnreset = true
                returndown = false
            } else if (angle > lower + 5 && angle < upper - 5 && reset) {
                returndown = false
                returnup = false
                returnreset = false
            }
        } else {
            if (angle!! < lower && !down) {
                returndown = true
            } else if (angle > upper && !up) {
                returnup = true
            } else if (angle < lower && down && up) {
                returncount = count + 1
                returnreset = true
                returnup = false
            } else if (angle > lower + 20 && angle < upper - 20 && reset) {
                returndown = false
                returnup = false
                returnreset = false
            }
        }
        return CountState(returnup, returndown, returnreset, returncount)
    }

    private fun getAngleThreePoint(
        keyPoints: ArrayList<Landmark>,
        poseIdx1: Int,
        poseIdx2: Int,
        poseIdx3: Int
    ): Double {
        val keypoint1: ArrayList<Double> = arrayListOf(keyPoints[poseIdx1].x, keyPoints[poseIdx1].y)
        val keypoint2: ArrayList<Double> = arrayListOf(keyPoints[poseIdx2].x, keyPoints[poseIdx2].y)
        val keypoint3: ArrayList<Double> = arrayListOf(keyPoints[poseIdx3].x, keyPoints[poseIdx3].y)

        return calculateAngle(keypoint1, keypoint2, keypoint3)
    }

    private fun calculateAngle(
        a: ArrayList<Double>,
        b: ArrayList<Double>,
        c: ArrayList<Double>
    ): Double {
        val vectorA: ArrayList<Double> = arrayListOf(a[0] - b[0], a[1] - b[1])
        val vectorB: ArrayList<Double> = arrayListOf(c[0] - b[0], c[1] - b[1])

        val distanceA = sqrt(vectorA[0] * vectorA[0] + vectorA[1] * vectorA[1])
        val distanceB = sqrt(vectorB[0] * vectorB[0] + vectorB[1] * vectorB[1])
        var product = 0.0
        for (i in vectorA.indices) {
            vectorA[i] /= distanceA
            vectorB[i] /= distanceB
            product += vectorA[i] * vectorB[i]
        }

        val radians = acos(product)
        return radians / PI * 180
    }

    companion object {
        private const val FRAME_CUT = 10
    }
}