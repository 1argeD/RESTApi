protobuf 도입 이유 : 송수신에 필요한 대역폭을 줄이는 것이 가능하며, 분산시스템의 성능 안전성을 향상 시킬 수 있다<br/>

protobuf를 도입할 경우 위도우에서 환경 변수를 설정해 주어야 사용이 가능하다. </br>
다운 받은 protobuf의 파일과 의존성의 버전이 맞지 않는 경우 버전마다 클래스명이 다르기 때문에 애러가 발생해 꼭 서로 호환되는 버전을 사용해야 한다.ex)protoc-27.2-win64 는 java.4.26.0 버전을 사용해야함</br>
<br/>
 protobuf 문법<br/>
 protc = --java_out = {자바로 임포트 할 주소} {proto 경로}<br/>
해당 환경변수 설정 후 함수를 실행하면 proto 파일이 java언어로 생성 되어 사용할 수 있다.


//hateoas는 서버와 클라이언트의 분리로 클라이언트는 서버의 행동을 알 필요가 없으며, 서버측에도 클라이언트에 하나하나 대응할 필요가 없어진다.<br/>
HAL(Hypertext Application Language) 링크의 정보를 JSON으로 표현하는 표준 통칭 HAL <br/>
	implementation  'org.springframework.boot:spring-boot-starter-actuater'<br/>
	implementation 'org.springframework.data:spring-data-rest-hal-brower'<br/>
 에서 implementation 'org.springframework.boot:spring-boot-starter-hateoas' 로 변경<br/>
 <br/>
hateoas의 ResourceAssmble 에서 RepresentationModelAssembler 로 클래스 명 변경 </br>
Resource<> 도 EntityModel<> 로 변경 되었으며,</br>
Resources<> 도 CollectionModel로 변경 되었다</br>
해당 클래스 함수는 전부 protected 처리가 되어 클래스를 of() 를 통해서 호출해야한다.</br>
기존 : EntityModle<Customer> customer = new EntityModle<>(Custmoer);<br>
변경후 : Customer c = new Coustomer();<br>
 EntityModle<Customer> customer = EntityModle.of(c);
 
