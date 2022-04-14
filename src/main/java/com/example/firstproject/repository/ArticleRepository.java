package com.example.firstproject.repository;

import com.example.firstproject.entity.Article;
import org.springframework.data.repository.CrudRepository;

//6-13. springboot 제공 CrudRepository는 CrudRepository<T, ID>로 정의된 인터페이스다.
// -> 인페를 구현없이 사용하려면, [인페로 extends] 상속하면, 내부기능들을 그대로 사용할 수 있게 된다.
// --> 만약, [인페를 class로 impl]하면, 구현체로서 싹다 구현하도록 빨간줄 뜸.
// --> 하지만, [springboot 인페를, 인페로 extends]만 해놓은 뒤, Controller에서 상속인페를 필드로+@애노테이션으로 자동초기화만 해주면
// --> springboot 인페 제공 repository 기능을 확장받아서 사용할 수 있게 된다.
//public interface ArticleRepository extends CrudRepository {

//6-14. CrudRepository의 제네릭에는 entity + id(대표값)의 Type을 지정해줘야한다.
public interface ArticleRepository extends CrudRepository<Article, Long> {
}
