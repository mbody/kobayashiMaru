package Jobs;

import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

/**
 * Created with IntelliJ IDEA.
 * User: christophe
 * Date: 07/02/12
 * Time: 11:23
 * To change this template use File | Settings | File Templates.
 */
@OnApplicationStart
public class DataLoader extends Job{


    public void doJob(){
        Fixtures.delete();
        Fixtures.loadModels("users.yml");
    }
    
}
