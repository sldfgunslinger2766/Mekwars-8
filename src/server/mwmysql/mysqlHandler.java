package server.mwmysql;

import server.mwmysql.MWmysql;
import server.mwmysql.planetHandler;
import server.mwmysql.factoryHandler;
import server.campaign.SUnitFactory;
import server.campaign.SPlanet;

public class mysqlHandler{
  private MWmysql MySQLCon = null;
  private planetHandler ph = null;
  private factoryHandler fh = null;

  public void closeMySQL(){
    MySQLCon.close();
  }

  public void deleteFactory(int FactoryID){
    fh.deleteFactory(FactoryID);
  }

  public void deletePlanetFactories(String planetName){
    fh.deletePlanetFactories(planetName);
  }

  public void saveFactory(SUnitFactory factory){
    fh.saveFactory(factory);
  }

  public void loadFactories(SPlanet planet){
    fh.loadFactories(planet);
  }

  public mysqlHandler(){
    this.MySQLCon = new MWmysql();
    this.ph = new planetHandler(MySQLCon.con);
    this.fh = new factoryHandler(MySQLCon.con);
  }
}
