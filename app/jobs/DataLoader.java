package jobs;

import play.Logger;
import play.Play;
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
        if(Play.configuration.get("application.mode").toString().startsWith("prod")){
            Logger.info("Pas de chargement des données en production !");
            return;
        }else{
            Logger.info("Chargement des données à partir du yaml !");
        }
        Fixtures.deleteAllModels();
        Fixtures.loadModels("devData.yml");
    }
}
