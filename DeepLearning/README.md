## Deep Learning
- 딥러닝 환경 재구축  
- 학습방향 설정
- CAM 활용
- Accuracy 정리
- 문법교정

### 딥러닝 환경 재구축
-오디세이 및 렌탈 PC
  - [x] 오디세이
  - [x] 렌탈 pc (ssd로 교체 및 환경 재설치)

### **적절한 학습방향을 잡기위한 테스트** (7.15 ~ 8.1)
-여러사람들의 인식률을 높히기 위한 방식
  - [x] 1. 병운이형의 영상을 train에 넣고 epoch 500번의 학습 진행 후, 학습된 가중치에 채현이 영상을 train에 넣고 epoch 500번의 학습진행. 총 epoch은 1000번
  - [x] 2. 병운이형, 채현이 영상을 한번에 train에 넣고, epoch 1000번의 학습 진행  
**=> 결과 : 2번의 방식(계속 이방향으로 학습을 진행할 예정)**  

### **frame을 늘려서 보다 긴 영상을 학습 할 수 있는지 테스트** (8.5 ~ 8.9)
  - [x] 1. 각 align 파일들에서 가능한 모든 단어들을 조합해서 하나의 grid.txt 파일에 저장하는 코드 작성
  - [x] 2. 기존 75frame의 영상과 align 파일들을 150frame의 영상(150_frame_extract_mouth_batch2.py)과 align(150frame_algin.py)으로 바꾸어 주는 코드 작성
  - [x] 3. 위의 1,2번 테스트 후 epoch 1000번의 학습 진행  
**=> 결과 : 75frame에서는 높은 정확도를 가지지만 150frame에서는 validation loss가 일정 수준으로 떨어지다가 다시 올라감. overfitting 되었다는 결론에 도달했다. 학습량을 충분히 더 늘려서 테스트해보아야함!**    

### **frame을 늘려서 보다 긴 영상을 학습 할 수 있는지 테스트** (8.12 ~ )
  - [x] 1. 모든 **단어**(야구, 볼펜, 필통, 매우, 늦게, 예쁜, 주황, 노랑, 다홍), **문장**(감사합니다, 좋아합니다, 안녕하세요 고생하세요)을 train 327개, val 98개 넣고 학습을 진행중  
**=> 결과 : 현재 epoch 500번 정도 한 결과 기존의 병운이형의 정확도가 매우 많이 올라갔다. 모든 문장및 단어에서 95%정도의 인식률을 기록!!**

### **CAM(Class Activation Map) 활용** (8.18 ~ )
  ![CAM 과정](https://user-images.githubusercontent.com/32935365/63221598-76b13d80-c1d6-11e9-9ef4-5c6508f0bfd9.png)  
  ![outputVideo](https://user-images.githubusercontent.com/32935365/63223313-3b226d80-c1ee-11e9-8d20-096909e73cdc.gif)
  - [x] CAM을 RESNET50을 이용하여 사용할 수 있는 환경 구축 및 테스트
  - [x] CAM을 활용하여 우리 모델을 사용하여 입술 처리 과정을 보여주는 영상 저장
  - [ ] CAM을 활용하여 우리 모델을 사용하여 입술 처리 과정을 보여주는 영상을 web 화면에서 보여줌

**=> 결과 : **

### **Accuracy 정리** ( ~ )
  - [ ] 각 모든 test 과정에서의 accuracy정리 **SER**(Sentence Error Rate), **WER**(Word Error Rate), **CER**(Character Error Rate)


### **문법 교정**
  - [x] 쓰세요
  - [ ] 쓰세요
