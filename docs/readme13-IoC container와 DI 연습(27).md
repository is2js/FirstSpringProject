### 제어의역전과 의존성 주입
![20220521114236](https://raw.githubusercontent.com/is2js/screenshots/main/20220521114236.png)

- 어떻게 객체 생성 없이 class를 사용할 수 있었을까?	
	![20220521114305](https://raw.githubusercontent.com/is2js/screenshots/main/20220521114305.png)
	- **springboot가 제공하는 `IoC container` 덕분이다.**
- 핵심 객체를 관리하는 창고
	- controller, service, repository 객체를 만들고 관리함
	- Ioc conainer 속 객체들은 필요한 객체들로 주입될 수 있다.
		- **개발자 코드가 아닌 IoC container에 의해 통제**
		![20220521114430](https://raw.githubusercontent.com/is2js/screenshots/main/20220521114430.png)
- **프로그램의 흐름이 `개발자코드가 아닌 외부에 의해 제어`되는 것을 `IoC(제어의 역전)`이라고 한다.**
- **이렇게 외부제어된 객체를 필요에의해 꼽아주는 것을 `DI(의존성 주입)`이라고 한다**

- IoC + DI는 객체간 결합을 낮춰서 더욱 유연한 코드가 되게 한다.
	![20220521114558](https://raw.githubusercontent.com/is2js/screenshots/main/20220521114558.png)


### 실습(확장되는 것을 객체 공장으로 객체필드에 DI 생성자 주입 + IoC container 등록)
- DI가 필요한 상황을 만들고 코드 개선
- IoC Conatiner에 객체 등록 -> 갖다 사용하는 것까지 실습
	![20220521114703](https://raw.githubusercontent.com/is2js/screenshots/main/20220521114703.png)

#### 확장되는 것을 객체 공장으로 객체필드에 DI 생성자 주입
1. ioc 패키지 > Chef class생성 > test부터 생성한다.
	![20220521114816](https://raw.githubusercontent.com/is2js/screenshots/main/20220521114816.png)
	![20220521114825](https://raw.githubusercontent.com/is2js/screenshots/main/20220521114825.png)

2. method test에서 `//then`에 해당하는 `1.수행`은 모든 것을 변수로 쓴다.
	- 객체, 메서드, 인자, 응답값 모두 변수로 작성한다.
		- **테스트 대상인 메서드만 미완성으로 red 뛰우고 가장 나중에 작성할 것이다. `나머지는  다 준비`한다.**
	- 객체 생성도 여기서 하지 않고 바로 instance로 사용한다
		![20220521115334](https://raw.githubusercontent.com/is2js/screenshots/main/20220521115334.png)
	- 참고로 객체생성자 생성은 ide가 자동으로 해주지 않으므로 -> local field로 뽑아내고 ->  `2. 준비(//given)`에서 직접 생성자호출해서 만들어준다.
		![20220521115440](https://raw.githubusercontent.com/is2js/screenshots/main/20220521115440.png)
		![20220521115533](https://raw.githubusercontent.com/is2js/screenshots/main/20220521115533.png)
		![20220521115544](https://raw.githubusercontent.com/is2js/screenshots/main/20220521115544.png)
		![20220521115602](https://raw.githubusercontent.com/is2js/screenshots/main/20220521115602.png)
	- 인자도 `2. 준비(//given)`에 줄 재료를 건네준다.
		![20220521115937](https://raw.githubusercontent.com/is2js/screenshots/main/20220521115937.png)
	- `3. 예상`은 메서드에서 변수로 반환할 그 값을 예상한 뒤, 우항에는 값 -> 좌항에는 expected라는 변수로 이름 짓는다.
		![20220521115950](https://raw.githubusercontent.com/is2js/screenshots/main/20220521115950.png)
	- `4. 검증`에는 메서드 응답값 - 그 값을 예상한 expected를 비교하도록 작성한다.
		- method빼고 다 green을 띄워놓는다.
			![20220521120156](https://raw.githubusercontent.com/is2js/screenshots/main/20220521120156.png)
		- assert문 아래에 **확인용 soutv를 찍어놓기도 한다.**
			![20220521120250](https://raw.githubusercontent.com/is2js/screenshots/main/20220521120250.png)
	- red를 띄우고 cook(테스트 목적 메서드)를 작성한다.
		- **method는 `예상값(expected)가 반환되도록 작성`한다.**

3. cook method 작성
	- menu는 요리하는 재료가 아니라 일종의 설계도다. 따로 요리 재료가 필요하다.
	- 재료도 주입받으면 좋겠지만, 내수용으로서 내부에 생성자로 생성한다. 
		![20220521125130](https://raw.githubusercontent.com/is2js/screenshots/main/20220521125130.png)
	- 돼지고기를 Pork 객체로 만든다. 
		- name만 필드로 가지며, name만 반환한다.
			![20220521125322](https://raw.githubusercontent.com/is2js/screenshots/main/20220521125322.png)

4. cook method 다른case 추가 -> `인자(menu)`가 달라져서 `다른 case`를 만든다.
	- 기존: 돈가스 요리하기
	- 추가: 스테이크 요리하기
	- ~~**method의 `인자` + 나올 응답값 `예상(값+변수)`가 달라진다**~~
	- ~~**추가 케이스는 `기존케이스 반환유지`하면서 `달라진 예상값`이 나오도록 작성**한다.~~

5. 문제점: 내수 재료인 돼지고기(Pork)와 소고기(Beef)의 **재료를 내부에서 만들다 보면**
	1. 원하는 응답이 나오려면, 내수재료를 수동적으로 주석처리하며 직접  바꿔줘야한다.
	2. 내부에 if문을 써서 `인자`마다 다른 `내수재료`를 사용할 수 도 있지만, 중복코드가 많이 생긴다.
		![20220521130716](https://raw.githubusercontent.com/is2js/screenshots/main/20220521130716.png)


6. **의존성 주입(DI) -> 재료를 method내에서 `내수하면 의존성이 커진다`**
	- **내부에서 사용되는 `재료들은 외부에서 주입`받을 예정이며**
	- **그 재료는 `외부 재료공장`을 만들어, 거기서 `조달`받는다.**
	- **my) 재료가 여러개가 된다? -> 재료공장을 따로 세운다.**
	- **my) `재료공장`은 일회성 지역변수인 파라미터가 되는 메서드에 주입하는 것이 아니라 `주체인 객체에 생성자 주입 -> 필드로 가져 -> 객체가 재료공장을 알고 의존하게 한다`**


7. Test에서 재료공장 만들어 method가 아닌 instance에 주입하기
	1. `1.준비(//given)`에서 재료공장을 생성자를 통해 껍데기만 만들어준다.
		![20220521131106](https://raw.githubusercontent.com/is2js/screenshots/main/20220521131106.png)
	2. **메서드에 재료주입이 아니라, `객체에 재료공장`을 주입한다.**
		- my) 신선함. 재료는 메서드에 **재료공장은 주체인 객체의 생성자에 주입 -> 필드로 가져 의존성을 가진다.(알게 한다)**
		![20220521131342](https://raw.githubusercontent.com/is2js/screenshots/main/20220521131342.png)
		![20220521132005](https://raw.githubusercontent.com/is2js/screenshots/main/20220521132005.png)

8. 재료공장은 가진 객체Chef는 내수재료들 -> 외부공장에서 재료조달 받도록 수정한다.
	- **주입받은 재료공장으로부터 여러종류의 재료들을 조달 받는데, method가 받아온 인자menu에 따라 다르게 조달할 수 있도록, 조달메서드()에 인자로 준다. `.조달메서드( argu )`**
		![20220521132324](https://raw.githubusercontent.com/is2js/screenshots/main/20220521132324.png)
	- **여러 종류를 1개의 변수에 조달 받는 방법은 `구상체들`을 `추상체`에 담는 것이다.**
		1. **Pork, Beef를 구상체로보고 -> `추상체(부모클래스or인터페이스)`를 하나 만들어줘야한다.**
			![20220521132513](https://raw.githubusercontent.com/is2js/screenshots/main/20220521132513.png)
		2. 꼼수를 이용하면 구상체들로 간주하는 것 중 1개로 들어가 메서드에 `@Override`를 달고, `추상체`를 만들어준 다음, 나머지것들도 상속하도록 해준다.
			- superclass로 추상체를 만들어주면, method+field+`생성자`까지 다 같이 올라가진다.
			- **`field+ 생성자`가 올라갔다면, 구상체의 field + 생성자를 삭제하고 `super()`로 초기화해야한다.**
			![20220521132627](https://raw.githubusercontent.com/is2js/screenshots/main/20220521132627.png)
			![20220521132635](https://raw.githubusercontent.com/is2js/screenshots/main/20220521132635.png)
			![20220521132653](https://raw.githubusercontent.com/is2js/screenshots/main/20220521132653.png)
			![20220521132704](https://raw.githubusercontent.com/is2js/screenshots/main/20220521132704.png)
			![20220521132720](https://raw.githubusercontent.com/is2js/screenshots/main/20220521132720.png)
			![20220521132805](https://raw.githubusercontent.com/is2js/screenshots/main/20220521132805.png)
			![20220521132818](https://raw.githubusercontent.com/is2js/screenshots/main/20220521132818.png)
			![20220521132919](https://raw.githubusercontent.com/is2js/screenshots/main/20220521132919.png)
			![20220521132926](https://raw.githubusercontent.com/is2js/screenshots/main/20220521132926.png)
		3. 이제 재료 조달메서드의 응답값은 추상체로 받아준다.
			![20220521133018](https://raw.githubusercontent.com/is2js/screenshots/main/20220521133018.png)
			![20220521133039](https://raw.githubusercontent.com/is2js/screenshots/main/20220521133039.png)
		4. **superclass는 `abstract`를 달아줘서 추상클래스로 만든다.**
			![20220521133149](https://raw.githubusercontent.com/is2js/screenshots/main/20220521133149.png)
		



9. 이제 재료공장에서 재료를 인자에 따라 다르게 반환해주는 조달메서드를 완성해보자.
	- 내부에서 if로 각 상황에 맞는 재료를 반환해준다.
	- swith-case-default를 활용할 수 도 있다.
		- case에 안걸리는 경우 return null;을 해준다.
		![20220521133430](https://raw.githubusercontent.com/is2js/screenshots/main/20220521133430.png)

	```java
	public class IngredientFactory {
		public Ingredient get(final String menu) {
			if (Objects.equals(menu, "돈가스")) {
				return new Pork("한돈 등심");
			}
			if (Objects.equals(menu, "스테이크")) {
				return new Beef("한돈 등심");
			}
			return null;
		}
	}
	```

10. 각 case마다 `1. 준비(//given)`에 재료공장부터 생성하여 -> 객체에 주입한다.
	![20220521140338](https://raw.githubusercontent.com/is2js/screenshots/main/20220521140338.png)


11. 결과적으로 **셰프 <-> 내수 재료들** 사이에 **의존성주입을 통한 객체에 `상황에 따라 다르게 재료조달해주는 재료공장`주입**을 통해 **의존성을 낮춰, 요구사항 변경에 유연해진다.**
	- 재료를 객체가 객체-메서드-내부에서 직접 만드는 것을 -> 공장객체를 삽입하고 객체-메서드-내부에서 상황에 따라 조달받도록 한 것이다.
	1. 내수(메서드 내부생성)하는 재료는 요구사항에 의해 늘어날 수 있다.
		- 내수를 하다보면, 메서드에 if문이 계속 생긴다
	2. 재료들을 `외부에서` && `1개`로 받아오기 위해 
		1. `재료공장`을 만든다.
		2. 재료 공장은 method가 아니라 `instance에 생성자 주입 -> 필드로 받아 의존`한다.
			- method가 아닌 `객체`가 의존하게 한다.
		3. 객체 내부 재료공장(필드)는 객체메서드 내에서 `재료를 1개로 조달해주는 .조달메서드(case)`를 통해 상황에 맞게 재료를 1개 변수로 조달한다.
		4. 재료공장 내부에서는 `case에 맞는 재료들`을 내어주기 위해 `재료들을 구상체`로 보고 `추상체`를 1개 만들어 상속/구현하게 한다.
			- 람다식으로 조달하려면 1개메서드를 가진 인터페이스를 추상체로 만들어야할 듯.
	3. 메서드는 case에 맞게 조달된 재료를 통해 1개 로직만 작성한다.
		![20220521140413](https://raw.githubusercontent.com/is2js/screenshots/main/20220521140413.png)
	4. 느낀점
		- method에서 `내부 생성재료들`은
			1. 객체 재료공장을 외부 생성자 주입받고
			2. `객체 의존(생성자주입->필드)`하는 재료공장으로 `내부 조달`
		- method에서 재료공장을 조달받으면 안되나?
			- method 호출시마다 공장 주입은 쫌 그렇다.
			- method 주인인 **객체에 공장을 미리 주입**해놓고
				- **method는 조달 case주문만 인자로 받는다.**
		- **`확장 가능성 있는 것(변하는 것)`은 DI 주입받는다 -> 재료공장**
		- **확장시 도메인은 그대로 있고, `도메인 외부`의 재료공장만 case로 추가한다**
	5. 득도
		- **`확장시 마다 바뀌는 코드`는 method 내부라도 `외부에서 -> 객체로 생성자주입된 뒤 필드`로 가지게 하여 -> `method내에서도 필드.조달메서드()`로 조달되게한다**
		- **만약, 확장시마다 여러 객체들이 택1로 조달되어야한다면, 구상체들로 간주하고 추상체를 만들어 외부재료공장에서 1개를 조달해주도록 해야한다.**


12. DI(객체 필드로 외부 재료공장 주입 -> 내부 조달 호출)를 통해 코드개선된 효과를 알아보기 위해 **확장 해보기**
	- 돈가스, 스테이크에 이어 크리스피 치킨을 만들도록 확장해보자.
	- **DI방식으로 코드를 개선한 상태라면 확장시 코드 추가가 최소화 된다.**
		- 외부주입으로 인해, 도메인의 변화는 없다고 보면 된다.
	1. 새로운 인자 + 예상값으로 테스트 red를 낸다
		- 재료공장에서 인자에 맞는 재료 반환 로직이 없기 때문이다.
		- 재료 공장만 확장해주면 된다.
		- 확장의 대상은 `도메인이 아닌 외부에서 주입되는 재료공장`만 코드가 변한다.
		![20220521153624](https://raw.githubusercontent.com/is2js/screenshots/main/20220521153624.png)
	
	

#### 재료공장과 도메인객체를 IoC container 등록후 가져오기

1. 등록은 생략하고, `재료공장`을 springBoot Ioc Container로부터 가져오도록 코드를 짜보자.
	1. Test에 `@SpringBootTest`를 달아준다.
	2. 각 테스트메서드별 재료공장의 `생성자 호출로  생성`부분을 주석처리하고, 테스트 class에 공통사용할 수 있게 필드 + `@Autowired`해놓고 돌려보자.
		![20220521155128](https://raw.githubusercontent.com/is2js/screenshots/main/20220521155128.png)
		- 등록은 안했지만 일단 땡겨와본다.
			![20220521155225](https://raw.githubusercontent.com/is2js/screenshots/main/20220521155225.png)
			![20220521155404](https://raw.githubusercontent.com/is2js/screenshots/main/20220521155404.png)
2. IoC conainer에 등록하는 방법은 `재료공장` class에 `@Component`만 달아주면 된다.
	- 해당 클래스를 spring bean(객체)로 만들고, 이것을 IoC 컨테이너에 등록함
	```java
	@Component // 해당 클래스를 spring bean(객체)로 만들고, 이것을 IoC 컨테이너에 등록함
	```
	![20220521155436](https://raw.githubusercontent.com/is2js/screenshots/main/20220521155436.png)
	![20220521155451](https://raw.githubusercontent.com/is2js/screenshots/main/20220521155451.png)
	![20220521155458](https://raw.githubusercontent.com/is2js/screenshots/main/20220521155458.png)

	- **매 테스트마다 공통적으로 생성해야하는 객체**의 `new 재료공장()`과정 없이 ->  `spring bean 객체`로 만들고 주입할 수 있게 `IoC Conainer에 등록`해서 -> `@Autoweird`로 가져다 쓸 수 있게 되었다.
		- **my) 자주 사용되는 객체의 `new 객체()`로 객체 생성하는 과정을 IoC에게 맡겨 관리하게 한다.**

3. 재료공장 뿐만 아니라 `Chef`객체도 IoC에서 관리되도록 해보자.
	1. 객체 생성 부분을 주석처리한다(제거)
		![20220521160315](https://raw.githubusercontent.com/is2js/screenshots/main/20220521160315.png)
	2. 해당 class에 @Component를 달아 IoC Container에 올린다.
		![20220521160343](https://raw.githubusercontent.com/is2js/screenshots/main/20220521160343.png)
		![20220521160046](https://raw.githubusercontent.com/is2js/screenshots/main/20220521160046.png)
	3. 사용하는 곳에 @Autoweired로 DI 한다





### 댓글

Simon사이먼
2개월 전
궁금한것이 있습니다 chef 클래스안에 생성자에서 IngredientFactory 를 전달 하게 되어있는데, @Component 어노테이션을 달면서 해당 행위를 하지 않아도 동작이 되더라고요.
Chef 클래스 안의 IngredientFactory  에도 @Autowired를 달아 주어야 하는것 아닌가요? 
왜 정상 동작 하는지 이해가 잘 되지 않네요 ..

- 컴포넌트로 등록된 클래스에 생성자가 1개인 경우, @Autowired가 자동으로 붙게 됩니다.
- 따라서 실제로 @Autowired가 생성자에 달리게 됩니다.

```java
@Autowired
public Chef(IngredientFactory ingredientFactory) {
    this.ingredientFactory = ingredientFactory;
}
```
- 더 자세한 내용은 다음 키워드로 구글링해보세요 "Spring 의존성 주입 3가지 방법"