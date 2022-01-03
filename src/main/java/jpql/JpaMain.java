package jpql;

import net.bytebuddy.implementation.bytecode.Addition;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class JpaMain {
    private EntityManager em;

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {

            Member member = new Member();
            member.setUsername("member1");
            member.setAge(10);
            em.persist(member);

            //TypeQuery  제네럴로 가진다.
            TypedQuery<Member> query = em.createQuery("select m from Member m", Member.class);
            //TypedQuery<String> query2 = em.createQuery("select m.username, m.age from Member m", String.class);
            Query query2 = em.createQuery("select m.username, m.age from Member m");
            // 타입정보를 받을 수 없을때는 query

            //여러개 반환
            List<Member> resultList = query.getResultList();

            for (Member member1 : resultList) {
                System.out.println("member1= " + member1);
            }

            //한개 반환
            Member singleResult = query.getSingleResult();
            //Spring Data JPA ->
            System.out.println(singleResult);

            //바인딩
            TypedQuery<Member> query3 = em.createQuery("select m from Member m where m.username=:username", Member.class);
            query3.setParameter("username", "member1");
            Member singleResult1 = query3.getSingleResult();

            System.out.println("singleResult1= " + singleResult1.getUsername());

            //체이닝
            Member query4 = em.createQuery("select m from Member m where m.username=:username", Member.class)
                    .setParameter("username", "member1")
                    .getSingleResult();
            System.out.println("====================================================================================");
            System.out.println("====================================================================================");


            //프로젝션
            em.flush();
            em.clear();

            List<Member> result = em.createQuery("select m from Member m", Member.class)
                    .getResultList();

            //조인이 나가기 때문에                    //select m.team from Member m x 이렇게 쓰지말고
//         List<Member> result = em.createQuery("select t from m join m.team t", Member.class)
//                 .getResultList();

            Member findMember = result.get(0);
            findMember.setAge(20);
            // 엔티티 프로젝트 하면하면 다관리된다


            //임베디드 프로젝션
//            em.createQuery("select o.address from Order o", Adress.class)
//                    .getResultList();

            //스칼라 막가져오는것
            em.createQuery("select distinct m.username, m.age from Member m")
                    .getResultList();

            //여러값을 조회하는법
//            List<Object[]> resultList2 =  em.createQuery("select  m.username, m.age from Member m")
//                    .getSingleResult();
//
//            Object[] result33 = resultList2.get(0);
//            System.out.println("username = " + result33[0]);
//            System.out.println("age = " + result33[1]);

            em.flush();
            em.clear();


            List<MemberDTO> singleResult2 = em.createQuery("select  new jpql.MemberDTO(m.username, m.age) from Member m", MemberDTO.class)
                    .getResultList();


            MemberDTO memberDTO = singleResult2.get(0);
            System.out.println("username = " + memberDTO.getUsername());
            System.out.println("age = " + memberDTO.getAge());


            //페이징

//            for (int i = 0; i < 100; i++) {
//
//                Member memberPaging = new Member();
//                member.setUsername("member1" + i);
//                member.setAge(i);
//                em.persist(memberPaging);
//
//            }
            em.flush();
            em.clear();

            List<Member> resultList1 = em.createQuery("select m from Member m order by m.age desc", Member.class)
                    .setFirstResult(1)
                    .setMaxResults(10)
                    .getResultList();

            System.out.println("result.size" + result.size());

            for (Member member1 : result) {
                System.out.println("membeer1" + member1);
            }


            //조인
            Team team = new Team();
            team.setName("teamA");
            em.persist(team);


            Member member1 = new Member();
            member1.setUsername("memberJounin");
            member1.setAge(10);

            member.setTeam(team);

            em.persist(member1);


            String queryJoin = "select m from Member m inner join m.team t";
            List<Member> resultListJoin = em.createQuery(queryJoin, Member.class)
                    .getResultList();


            em.flush();
            em.clear();




            //JPQL 타입과 표현식

            Member member2 = new Member();
            member2.setUsername("memberType");
            member2.setAge(14);
            member2.setType(MemberType.ADMIN);

            member2.setTeam(team);

            em.persist(member2);
            em.flush();
            em.clear();

            String queryType = "select m.username, 'HELLO', true From Member m "+
                               "where m.type = :userType";
                                //jpql.MemberType.ADMIN  == :userType

            List<Object[]> result2 = em.createQuery(queryType)
                    .setParameter("userType", MemberType.ADMIN )
                    .getResultList();


            for (Object[] objects: result2) {
                System.out.println("objct" + objects[0]);
                System.out.println("objct" + objects[1]);
                System.out.println("objct" + objects[2]);
            }

            Member member3 = new Member();
            member3.setUsername("관리1자");
            em.persist(member3);

            Member member4 = new Member();
            member4.setUsername("관리자4");
            em.persist(member4);


            em.flush();
            em.clear();

            //String queryFunction = "select 'a' || 'b' From Member M";


            String queryFunction = "select function('group_concat', m.username ) From Member m";

            List<String> resultFunction = em.createQuery(queryFunction, String.class)
                    .getResultList();

            for (String s : resultFunction){

                System.out.println("S = " + s);
            }

            // 페치조인
            System.out.println("=============================================================");

            Team teamA = new Team();
            teamA.setName("팀A");
            em.persist(teamA);

            Team teamB = new Team();
            teamB.setName("팀B");
            em.persist(teamB);

            Member member5 = new Member();
            member5.setUsername("회원페치조인1");
            member5.setTeam(teamA);
            em.persist(member5);

            Member member6 = new Member();
            member6.setUsername("회원페치조인2");
            member6.setTeam(teamA);
            em.persist(member6);

            Member member7 = new Member();
            member7.setUsername("회원페치조인3");
            member7.setTeam(teamB);
            em.persist(member7);

            em.flush();
            em.clear();

           // String pathQuery = "select m From Member m join fetch m.team" ;
                    // 컬렉션 select distinct t From team t join fetch t.members
                    // 페이징가져오는법 select m from Member m join fetch m.team t  이런식으로 맴버에서 시작하는 뒤집는 해결방안 1
            String pathQuery = "select t From Team t" ;
            //List<Member> pathResult = em.createQuery(pathQuery, Member.class)
            List<Team> pathResult = em.createQuery(pathQuery, Team.class)
                    .setFirstResult(0)
                    .setMaxResults(2)
                    .getResultList();

//            for (Member pathMember : pathResult) {
//                System.out.println(pathMember);
//                //회원1 , 팀A SQL
//                //회원2, 팀A 1차캐시로 가져온다.
//                //회원3 , 팀B는 쿼리가 나가서 가져온다.
//                // 이렇게 될경우 쿼리가 100명 조회될 경우 쿼리가 100번 나가게 된다. N + 1 이라고한다.
//                // 첫번째 날리는 쿼리만큼 발생하는 문제이다 이것은 페치조인으로 해결해야한다.
//            }

            for (Team team1 : pathResult) {
                System.out.println("team=" + team1.getName() + "|membets"+ team1.getId());
                for (Member member8 : team1.getMembers()) {
                                        System.out.println("-> member"+ member8);
                }
            }



            //named  쿼리

            List<Member> resultList2 = em.createNamedQuery("Member.findByUsername", Member.class)
                    .setParameter("username", "회원페치조인1")
                    .getResultList();

            for (Member member8 : resultList2) {
                System.out.println("member88"+ member8);
            }
            //FLUSH 자동 호출 commit, 할때 오토니까
            int resultCount = em.createQuery("update Member m set m.age = 20")
                    .executeUpdate();

            System.out.println("resultCount: "+ resultCount);

            tx.commit();
        } catch (Exception e) {

            e.printStackTrace();
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();


    }
}
