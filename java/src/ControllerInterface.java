import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;

public interface ControllerInterface
{
  public void addCDO(CDO oo);
    public CDO createCDO();
  public void killCDO(CDO cdoxx);
 public void setsectors(CDO cdox,List sectorsxx);
   public void addsectors(CDO cdox,Sector sectorsxx);
   public void removesectors(CDO cdox,Sector sectorsxx);
    public void addSector(Sector oo);
    public Sector createSector();
  public void killSector(Sector sectorxx);
  public void addStatFunc(StatFunc oo);
    public StatFunc createStatFunc();
  public void killStatFunc(StatFunc statfuncxx);
}

