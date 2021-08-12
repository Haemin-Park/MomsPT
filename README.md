# MomsPT

## Language
`Kotlin`

## Architecture
`MVVM`

## Technical Stack
`AAC`, `Databinding`, `MediaPipe`, `CameraX`, `ExoPlayer`, `Coroutine`, `Filament`, `Retrofit2`, `Gson`, `RxJava3`, `Okhttp3`, `Glide4`

## Key features currently implemented

### Today's recommended exercise
  - 추천 운동 목록 구현
  - 운동 상세 페이지 구현

### AI assit workout
  - `Mediapipe Pose`를 이용해 사용자 신체의 33개 키포인트를 예측하여 디바이스 화면에 표시 구현
  - 운동 영상 스트리밍 구현
  - 운동 영상의 키포인트와 사용자 신체의 키포인트를 유사도 분석 후 결과로 산출된 점수 표시 구현
  - 사용자가 획득한 점수 구간 별 `Bad`, `Great!`, `Perfect!` 코멘트 구현

### Body analysis
  - 디바이스에서 10초간 사용자의 모습을 녹화한 영상을 분석 서버로 전송 구현
  - 분석 서버로부터 분석 결과인 `GLB` 파일을 전송 받아 사용자 디바이스에 다운로드 구현
  - 다운로드한 `GLB` 파일을 `AR` 화면에 로드 구현

## ScreenShot

### Today's recommended exercise
<img src="https://user-images.githubusercontent.com/54823396/129127396-e4e827bf-ec0b-4ba0-8eb2-0fe428bc3df8.jpeg" width="22%"/>
<img src="https://user-images.githubusercontent.com/54823396/129127415-674f4f4a-1c0a-4577-bf54-56a677f7e7a3.jpeg" width="22%"/>
<img src="https://user-images.githubusercontent.com/54823396/129127418-c6f837bb-c7ed-4099-a9ed-da66f7beb97a.jpeg" width="22%"/>
<img src="https://user-images.githubusercontent.com/54823396/129127400-f8fbd83b-0cb0-4cff-94e5-ff2b30ad7677.jpeg" width="22%"/>

### AI assit workout
![화면-기록-2021-08-12-오전-11 38 37](https://user-images.githubusercontent.com/54823396/129130116-e7e30ccb-ccfc-4923-8fff-a9f3a66958c0.gif)

### Body analysis
<img width="22%" alt="1" src="https://user-images.githubusercontent.com/54823396/129129148-5c19eaf6-fcae-43d7-8ea9-ee2b945ba055.png">
<img width="22%" alt="2" src="https://user-images.githubusercontent.com/54823396/129129152-4d58dc6c-c65d-4efd-9468-4bfa4ec84a6a.png">
<img width="22%" alt="3" src="https://user-images.githubusercontent.com/54823396/129129153-63433a33-daee-4af1-9c70-1db55621e5a7.png">
<img width="22%" alt="4" src="https://user-images.githubusercontent.com/54823396/129129155-ba612b58-4d34-4ace-8ed5-3a2baf258d7e.png">
