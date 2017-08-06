
import pers.younghan.umizoo.common.ServiceLocator;
import pers.younghan.umizoo.configurations.Configuration;
import pers.younghan.umizoo.configurations.ConnectionMode;
import pers.younghan.umizoo.infrastructure.TextSerializerImpl;
import pers.younghan.umizoo.messaging.*;
import pers.younghan.umizoo.samples.commands.RegisterUser;
import pers.younghan.umizoo.samples.queries.FindAllUser;
import pers.younghan.umizoo.samples.readmodels.UserModel;

import java.util.Collection;
import java.util.HashMap;


public class Test {


    public static void main(String[] args) throws InterruptedException {

        Configuration.create().loadPackages("pers.younghan.umizoo.samples").enableService(ConnectionMode.Local).done();


        RegisterUser command = new RegisterUser("young.han", "hanyang", "19126332@qq.com");
        CommandResult commandResult = ServiceLocator.getInstance(CommandService.class).execute(command, CommandReturnMode.EventHandled);

        Thread.sleep(2000);
        System.out.format("command result:%s %n", TextSerializerImpl.instance.serialize(commandResult)).println();

//        commandResult = ServiceLocator.getInstance(CommandService.class).execute(command, CommandReturnMode.EventHandled);
//        Thread.sleep(2000);
//        System.out.format("command result:%s %n", commandResult.getStatus()).println();

        FindAllUser query = new FindAllUser();
        QueryResult queryResult = ServiceLocator.getInstance(QueryService.class).fetch(query);
        Thread.sleep(2000);
        System.out.format("query result:%s %n", TextSerializerImpl.instance.serialize(queryResult.getData())).println();
    }
}
