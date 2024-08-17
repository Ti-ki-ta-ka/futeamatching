# 빌드된 JAR 파일 찾기
BUILD_JAR=$(ls /home/ubuntu/.ssh/futeamatching/build/libs/*.jar)
JAR_NAME=$(basename $BUILD_JAR)
echo ">>> 빌드 파일명: $JAR_NAME" >> /home/ubuntu/deploy.log

# 빌드 파일 복사
DEPLOY_PATH=/home/ubuntu/.ssh/futeamatching/
cp $BUILD_JAR $DEPLOY_PATH

echo ">>> 현재 실행 중인 애플리케이션 PID 확인 후 종료" >> /home/ubuntu/deploy.log
CURRENT_PID=$(pgrep -fl $JAR_NAME | grep java | awk '{print $1}')

if [ -z "$CURRENT_PID" ]; then
    echo ">>> 실행 중인 애플리케이션이 없습니다." >> /home/ubuntu/deploy.log
else
    echo ">>> kill -15 $CURRENT_PID" >> /home/ubuntu/deploy.log
    kill -15 $CURRENT_PID
    sleep 5
fi

# 새로운 JAR 파일 배포
DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
echo ">>> DEPLOY_JAR 배포" >> /home/ubuntu/deploy.log
echo ">>> $DEPLOY_JAR을 실행합니다." >> /home/ubuntu/deploy.log

nohup java -jar $DEPLOY_JAR >> /home/ubuntu/deploy.log 2>> /home/ubuntu/deploy_err.log &
