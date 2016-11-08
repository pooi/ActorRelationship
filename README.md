<h1 align=center>ActorRelationship</h1>
<p align=center></p>
<br>

## Introduce
Actor Relationship은 어느 한 영화인이 다른 영화인한테 같이 출연하자고 제의하는 것이 얼마나 어려울 지를 구하는 프로그램입니다.
<br>
<br>
## precondition
<OL>
  <li>영화인의 인맥형성은 같은 작품 출연 여부이다.
  <li>최근에 함께 출연했을 경우 친밀도가 높다.
  <li>친밀도는 현재년도 – 영화개봉년도 + 1로 정의할 수 있다.
  <li>친밀도는 낮은 숫자일수록 높다.
  <li>영화를 최근에 다시 찍었을 경우 친밀도는 누적이 아니라 갱신 된다.
  <li>주연, 조연, 감동 등의 비중은 따지지 않는다.
</OL>
<br>
## Method
<ul>
<li>누적연수를 구하기 위해 Floyd 알고리즘을 활용합니다.
</ul>
```java
for(int k=0; k<actorList.size(); k++){
	for(int i=0; i<actorList.size(); i++){
		for(int j=0; j<actorList.size(); j++){
		  
			if(dGraph[i][k] + dGraph[k][j] < dGraph[i][j]){
				pGraph[i][j] = k;
				dGraph[i][j] = dGraph[i][k] + dGraph[k][j];
			}
			
		}
	}
}
```
<br><br>

## Usage
이 프로그램을 사용하기 위해서는 미리 만들어둔 <a href="https://github.com/pooi/ActorRelationship/blob/master/ActorRelationship/movieDB.data">영화DB</a>가 필요합니다.
<br><br>

## Run Screen
<img src="https://github.com/pooi/ActorRelationship/blob/master/001.PNG">
<br><br>

## Conclusion
두 사람과의 누적연수가 8년일 경우, 8년 전에 만난 적이 있는 사람한테 같이 출연하자는 제의하는 것과 같은 의미로 볼 수 있습니다.
