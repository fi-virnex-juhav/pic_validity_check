echo "Hello, I am clean_docker.sh file for pic_validity_check app by juhav."
echo "I stop & delete pic_validity_check container"
echo "I delete pic_validity_check image" 
echo "I copy pic_validity_check.jar file from target folder to src/main/docker folder"
echo "I was started by user $(whoami)"
echo ""
docker stop pic_validity_check
docker rm pic_validity_check
docker rmi -f pic_validity_check
#docker rmi -f pic_validity_check:latest
cp ~/eclipse-space/pic_validity_check/target/pic_validity_check.jar ~/eclipse-space/pic_validity_check/src/main/docker/
echo "output from ls -l :"
ls -l  ~/eclipse-space/pic_validity_check/src/main/docker/
/pic_validity_check/target/pic_validity_check.jar
