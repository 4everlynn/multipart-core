import core.MultipartData;
import org.junit.Test;
import support.Action;
import support.Human;

/**
 * @author 4everlynn
 * @version V1.0
 * @date 2020-04-26
 */
public class Core {
    @Test
    public void core() {
        MultipartData multipartData = new MultipartData();

        Human human = new Human()
                .setAge(18)
                .setName("Apache")
                .setGender("boy");

        Action swim = new Action().setName("swim");

        multipartData
                .parties(human);

        // get original obj
        System.out.println(multipartData.get(Human.class));

        // only retain field name
        System.out.println(
                multipartData
                        .retain(Human::getName)
                        .get(Human.class)
        );

        // swim is a @ProcessExtraExpression enabled object
        multipartData.parties(swim);

        System.out.println(multipartData);

        System.out.println(multipartData.get(Action.class));

    }
}
