import java.util.List;
import java.util.Date;
import java.util.Vector;

import java.lang.*;
import java.lang.reflect.*;
import java.util.StringTokenizer;
import java.io.*;



class CDO
        implements SystemTypes
{
    private double ps0 = 0; // internal
    private static List sectors = new Vector(); // of Sector

    private static List borrowers = new Vector(); // all borrowers
    private static List borrowerInSectors = new Vector(); // all relationship between borrowers and sectors

    private double sigma = 0;
    private double variance = 0;
    private double expectedLoss = 0;

    public CDO()
    {
        this.ps0 = 0;                         //probability of ZERO default

    }

    public String toString()
    { String _res_ = "(CDO) ";
        _res_ = _res_ + ps0;
        return _res_;
    }

    public void setps0(double ps0_x) { ps0 = ps0_x;  }                //set rate of default to ps0_x

    public static void setAllps0(List cdos,double val)                //set rate of default of a list of CDOs
    { for (int i = 0; i < cdos.size(); i++)
    { CDO cdox = (CDO) cdos.get(i);
        Controller.inst().setps0(cdox,val); } }

    public void setsectors(List sectorsxx) { sectors = sectorsxx;      // set sectors
    }

    public void setsectors(int ind_x,Sector sectorsxx) { sectors.set(ind_x,sectorsxx); } // set sectors

    public void addsectors(Sector sectorsxx) { sectors.add(sectorsxx);                    //add new sector
    }

    public void removesectors(Sector sectorsxx) { Vector _removedsectorssectorsxx = new Vector();
        _removedsectorssectorsxx.add(sectorsxx);
        sectors.removeAll(_removedsectorssectorsxx);
    }                                                                                      //remove sector

    public static void setAllsectors(List cdos,List _val)
    { for (int _i = 0; _i < cdos.size(); _i++)
    { CDO cdox = (CDO) cdos.get(_i);
        Controller.inst().setsectors(cdox, _val); } }                                      //set all sectors

    public static void setAllsectors(List cdos,int _ind,Sector _val)
    { for (int _i = 0; _i < cdos.size(); _i++)
    { CDO cdox = (CDO) cdos.get(_i);
        Controller.inst().setsectors(cdox,_ind,_val); } }                                   //set all sectors

    public static void addAllsectors(List cdos,Sector _val)
    { for (int _i = 0; _i < cdos.size(); _i++)
    { CDO cdox = (CDO) cdos.get(_i);
        Controller.inst().addsectors(cdox, _val); } }                                       //set all sectors

    public static void removeAllsectors(List cdos,Sector _val)
    { for (int _i = 0; _i < cdos.size(); _i++)
    { CDO cdox = (CDO) cdos.get(_i);
        Controller.inst().removesectors(cdox, _val); } }                                    //remove all sectors

    public static void unionAllsectors(List cdos, List _val)
    { for (int _i = 0; _i < cdos.size(); _i++)
    { CDO cdox = (CDO) cdos.get(_i);
        Controller.inst().unionsectors(cdox, _val); } }

    public static void subtractAllsectors(List cdos, List _val)
    { for (int _i = 0; _i < cdos.size(); _i++)
    { CDO cdox = (CDO) cdos.get(_i);
        Controller.inst().subtractsectors(cdox, _val); } }

    public double getps0() { return ps0; }                                                      // get value ps

    public static List getAllps0(List cdos)
    { List result = new Vector();
        for (int i = 0; i < cdos.size(); i++)
        { CDO cdox = (CDO) cdos.get(i);
            if (result.contains(new Double(cdox.getps0()))) { }
            else { result.add(new Double(cdox.getps0())); } }
        return result; }                                                            // get valve ps from a list of cdos

    public static List getAllOrderedps0(List cdos)
    { List result = new Vector();
        for (int i = 0; i < cdos.size(); i++)
        { CDO cdox = (CDO) cdos.get(i);
            result.add(new Double(cdox.getps0())); }
        return result; }

    public List getsectors() { return (Vector) ((Vector) sectors).clone(); }

    public static List getAllsectors(List cdos)
    { List result = new Vector();
        for (int _i = 0; _i < cdos.size(); _i++)
        { CDO cdox = (CDO) cdos.get(_i);
            result = Set.union(result,cdox.getsectors()); }
        return result; }

    public static List getAllOrderedsectors(List cdos)
    { List result = new Vector();
        for (int _i = 0; _i < cdos.size(); _i++)
        { CDO cdox = (CDO) cdos.get(_i);
            result.addAll(cdox.getsectors()); }
        return result; }

    public double nocontagion(int k,int m)
    {   double result = 0;

        result = Math.pow(( 1 - ((Sector) sectors.get(k - 1)).getp() ),((Sector) sectors.get(k - 1)).getn() - m) * Math.pow(((Sector) sectors.get(k - 1)).getp(),m) * Math.pow(( 1 - ((Sector) sectors.get(k - 1)).getq() ),m * ( ((Sector) sectors.get(k - 1)).getn() - m ));
        return result;
    }                                                                // Davis and Lo distribution, m is number of default, k is the sector ID


    public double P(int k,int m)
    {   double result = 0;

      result = StatFunc.comb(((Sector) sectors.get(k - 1)).getn(),m) * ( ((Sector) sectors.get(k - 1)).nocontagion(m) + Set.sumdouble(Set.collect_0(Set.integerSubrange(1,m - 1),this,k,m)) );
      return result;
    }


    public double PCond(int k,int m)
    {   double result = 0;

    if (m >= 1)
    {   result = this.P(k,m) / ( 1 - Math.pow(( 1 - ((Sector) sectors.get(k - 1)).getp() ),((Sector) sectors.get(k - 1)).getn()) );

    }  else   if (m < 1)
    {   result = 0;

    }    return result;
    }


    public int maxfails(int k,int s)
    {   int result = 0;

    if (((Sector) sectors.get(k - 1)).getn() <= ( s / ((Sector) sectors.get(k - 1)).getL() ))
    {   result = ((Sector) sectors.get(k - 1)).getn();

    }  else   if (((Sector) sectors.get(k - 1)).getn() > ( s / ((Sector) sectors.get(k - 1)).getL() ))
    {   result = s / ((Sector) sectors.get(k - 1)).getL();

    }    return result;
    }

    // Todo: test
    public int maxfails(int k, double s)
    {   int result = 0;

      if (((Sector) sectors.get(k - 1)).getn() <= (int)Math.floor(s / ((Sector) sectors.get(k - 1)).getL()))
      {   result = ((Sector) sectors.get(k - 1)).getn();

      }  else   if (((Sector) sectors.get(k - 1)).getn() > ( s / ((Sector) sectors.get(k - 1)).getL() ))
      {   result = (int)Math.floor(s / ((Sector) sectors.get(k - 1)).getL());

      }    return result;
    }

    public double PS(int s)
    {   double result = 0;
    Object cached_result = PS_cache.get(new Integer(s));
    if (cached_result != null)
    { result = ((Double) cached_result).doubleValue();
      return result;
    }
    else
    {   if (s < 0)
    {   result = 0;

    }  else   if (s == 0)
    {   result = ps0;

    }  else   if (s > 0)
    {   result = Set.sumdouble(Set.collect_1(Set.integerSubrange(1,sectors.size()),this,s)) / s;

    }
      PS_cache.put(new Integer(s), new Double(result));
    }
    return result;
   }                                          //9.5.1 Probability when loss = s of all sectors


    public double VS(int k,int s)
    {   double result = 0;

    result = Set.sumdouble(Set.collect_2(Set.integerSubrange(1,this.maxfails(k,s)),this,k,s));
      return result;
    }                                         //9.5.1 Probability when loss = s of a certain sector

    // Todo: test
    public double PS(double s)
    {   double result = 0;
      Object cached_result = PS_cache.get(s);
      if (cached_result != null)
      { result = ((Double) cached_result).doubleValue();
        return result;
      }
      else
      {   if (s < 0)
      {   result = 0;

      }  else   if (s == 0)
      {   result = ps0;

      }  else   if (s > 0)
      {   result = Set.sumdouble(Set.collect_1(Set.integerSubrange(1,sectors.size()),this,s)) / s;

      }
        PS_cache.put(s, new Double(result));
      }
      return result;
    }

    // Todo: test
    public double VS(int k, double s)
    {   double result = 0;

      result = Set.sumdouble(Set.collect_2(Set.integerSubrange(1,this.maxfails(k,s)),this,k,s));
      return result;
    }

    // Todo: test
    public double expectedLoss() {
      double result = 0;
      for (int k = 1; k <= sectors.size(); k++) {
        Sector sector = (Sector) sectors.get(k-1);
        double sum = 0;
        for (int m = 1; m <= sector.getn(); m++) {
          sum += m*P(k,m);
        }
        result += sector.getL()*sum;
      }
      return result;
    }

  // Todo: test
  public double varianceOfLoss() {
    double result = 0;
    for (int k = 1; k <= sectors.size(); k++) {
      Sector sector = (Sector) sectors.get(k-1);
      for (int m = 1; m <= sector.getn(); m++) {
        result += Math.pow(m,2)*Math.pow(sector.getL(),2)*P(k,m);
      }
    }
    return result;
  }

  // Todo: test
  public double sigmaOfLoss(){
      return Math.sqrt(varianceOfLoss());
  }

  // Todo: test
  public double riskContribution(int k) {
      Sector sector = (Sector) sectors.get(k-1);
      double result = 0;
      for (int m = 1; m <= sector.getn(); m++) {
        result += Math.pow(sector.getL(),2)*Math.pow(m,2)*P(k,m);
      }
      return result/sigmaOfLoss();
  }

  // Todo: test
  public double borrowerRiskContribution(Borrower borrower) {
    List thisBorrowerInSectors = borrower.getBorrowerInSectors();
    List sectorsOfBorrower = borrower.getSectors();

    double result = 0;
    for (int k = 1; k <= sectorsOfBorrower.size(); k++) {
        Sector sector = (Sector) sectorsOfBorrower.get(k);
        List allBorrowerInSector = sector.getBorrowerInSectors();
        BorrowerInSector borrowerInSector = borrower.getBorrowerInSectorFrom(sector);
        double inner = 0;
        for (Object borrowerInSectorJ: allBorrowerInSector){
            if (borrowerInSectorJ.equals(borrower)) continue;
            inner += ((BorrowerInSector) borrowerInSectorJ).collect1();
        }
        inner += (borrowerInSector.collect2() + borrowerInSector.collect1()*inner);
        double mProduct = 0;
        for (int m = 1; m < sector.getn(); m++) {
            mProduct = Math.pow(m,2);// Todo: complete with index k in all sectors but not sectorsOfBorrowers
        }
    }
    return result;
  }

    public BorrowerInSector getBorrowerInSector(int k, int a) {
      Borrower borrowerA = (Borrower) borrowers.get(a-1);
      Sector sectorK = (Sector) sectors.get(k-1);
    return borrowerA.getBorrowerInSectorFrom(sectorK);
  }

    public void test1(Sector s)
    { Controller.inst().setmu(s,1 - Math.pow(( 1 - s.getp() ),s.getn()));
    }

    public void test1outer()
    {  CDO cdox = this;
      List _range1 = cdox.getsectors();
    for (int _i0 = 0; _i0 < _range1.size(); _i0++)
    { Sector s = (Sector) _range1.get(_i0);
         this.test1(s);
    }
    }


    public void test2()
    { Controller.inst().setps0(this,Math.exp(-Set.sumdouble(Sector.getAllOrderedmu(this.getsectors()))));
    }

    public void test3()
    {     List _integer_list2 = new Vector();
      _integer_list2.addAll(Set.integerSubrange(0,50));
      for (int _ind3 = 0; _ind3 < _integer_list2.size(); _ind3++)
      {
        int s = ((Integer) _integer_list2.get(_ind3)).intValue();
          System.out.println("" + this.PS(s));

      }

    }

  private  java.util.Map PS_cache = new java.util.HashMap();

}

class Sector
  implements SystemTypes
{
  private String name = ""; // internal
  private int n = 0; // internal
  private double p = 0; // internal
  private double q = 0; // internal
  private int L = 0; // internal
  private double mu = 0; // internal

  private List borrowerInSectors = new Vector();
  private CDO cdo = null;

  public void setBorrowerInSectors(List borrowerInSectors) {
    this.borrowerInSectors = borrowerInSectors;
  }

  public List getBorrowerInSectors() {
      return borrowerInSectors;
  }

  public Sector()
  {
    this.name = "";
    this.n = 0;
    this.p = 0;
    this.q = 0;
    this.L = 0;
    this.mu = 0;

  }

  public CDO getCdo() {
    return cdo;
  }

  public void setCdo(CDO cdo) {
    this.cdo = cdo;
  }

  public Sector(String name, List borrowerInSectors) {
    this.name = name;
    this.borrowerInSectors = borrowerInSectors;
    this.n = borrowerInSectors.size();
    calculatePfromBorrowers();
    calculateQfromBorrowers();
    calculateLfromBorrowers();
    calculateMufromBorrowers();
  }

  private void calculatePfromBorrowers() {
    for (int _borrowerInSectors=0; _borrowerInSectors<n; _borrowerInSectors++) {
      BorrowerInSector borrowerInSector = ((BorrowerInSector)borrowerInSectors.get(_borrowerInSectors));
      L+=borrowerInSector.getP()*borrowerInSector.getOmega()*borrowerInSector.getTheta();
    }
  }

  private void calculateQfromBorrowers() {

  }

  private void calculateLfromBorrowers() {
    for (int _borrowerInSectors=0; _borrowerInSectors<n; _borrowerInSectors++) {
      BorrowerInSector borrowerInSector = ((BorrowerInSector)borrowerInSectors.get(_borrowerInSectors));
      L+=borrowerInSector.getL()*borrowerInSector.getOmega()*borrowerInSector.getTheta();
    }
  }

  private void calculateMufromBorrowers() {

  }

  public void addborrowerInSectors(BorrowerInSector borrowerInSector) {
    borrowerInSectors.add(borrowerInSector);
  }

  public String toString()
  { String _res_ = "(Sector) ";
    _res_ = _res_ + name + ",";
    _res_ = _res_ + n + ",";
    _res_ = _res_ + p + ",";
    _res_ = _res_ + q + ",";
    _res_ = _res_ + L + ",";
    _res_ = _res_ + mu;
    return _res_;
  }

  public void setname(String name_x) { name = name_x;  }


  public static void setAllname(List sectors,String val)
{ for (int i = 0; i < sectors.size(); i++)
    { Sector sectorx = (Sector) sectors.get(i);
      Controller.inst().setname(sectorx,val); } }


  public void setn(int n_x) { n = n_x;  }


  public static void setAlln(List sectors,int val)
{ for (int i = 0; i < sectors.size(); i++)
    { Sector sectorx = (Sector) sectors.get(i);
      Controller.inst().setn(sectorx,val); } }


  public void setp(double p_x) { p = p_x;  }


  public static void setAllp(List sectors,double val)
{ for (int i = 0; i < sectors.size(); i++)
    { Sector sectorx = (Sector) sectors.get(i);
      Controller.inst().setp(sectorx,val); } }


  public void setq(double q_x) { q = q_x;  }


  public static void setAllq(List sectors,double val)
{ for (int i = 0; i < sectors.size(); i++)
    { Sector sectorx = (Sector) sectors.get(i);
      Controller.inst().setq(sectorx,val); } }


  public void setL(int L_x) { L = L_x;  }


  public static void setAllL(List sectors,int val)
{ for (int i = 0; i < sectors.size(); i++)
    { Sector sectorx = (Sector) sectors.get(i);
      Controller.inst().setL(sectorx,val); } }


  public void setmu(double mu_x) { mu = mu_x;  }


  public static void setAllmu(List sectors,double val)
{ for (int i = 0; i < sectors.size(); i++)
    { Sector sectorx = (Sector) sectors.get(i);
      Controller.inst().setmu(sectorx,val); } }


  public String getname() { return name; }

  public static List getAllname(List sectors)
  { List result = new Vector();
    for (int i = 0; i < sectors.size(); i++)
    { Sector sectorx = (Sector) sectors.get(i);
      if (result.contains(sectorx.getname())) { }
      else { result.add(sectorx.getname()); } }
    return result; }

  public static List getAllOrderedname(List sectors)
  { List result = new Vector();
    for (int i = 0; i < sectors.size(); i++)
    { Sector sectorx = (Sector) sectors.get(i);
      result.add(sectorx.getname()); } 
    return result; }

  public int getn() { return n; }

  public static List getAlln(List sectors)
  { List result = new Vector();
    for (int i = 0; i < sectors.size(); i++)
    { Sector sectorx = (Sector) sectors.get(i);
      if (result.contains(new Integer(sectorx.getn()))) { }
      else { result.add(new Integer(sectorx.getn())); } }
    return result; }

  public static List getAllOrderedn(List sectors)
  { List result = new Vector();
    for (int i = 0; i < sectors.size(); i++)
    { Sector sectorx = (Sector) sectors.get(i);
      result.add(new Integer(sectorx.getn())); } 
    return result; }

  public double getp() { return p; }

  public static List getAllp(List sectors)
  { List result = new Vector();
    for (int i = 0; i < sectors.size(); i++)
    { Sector sectorx = (Sector) sectors.get(i);
      if (result.contains(new Double(sectorx.getp()))) { }
      else { result.add(new Double(sectorx.getp())); } }
    return result; }

  public static List getAllOrderedp(List sectors)
  { List result = new Vector();
    for (int i = 0; i < sectors.size(); i++)
    { Sector sectorx = (Sector) sectors.get(i);
      result.add(new Double(sectorx.getp())); } 
    return result; }

  public double getq() { return q; }

  public static List getAllq(List sectors)
  { List result = new Vector();
    for (int i = 0; i < sectors.size(); i++)
    { Sector sectorx = (Sector) sectors.get(i);
      if (result.contains(new Double(sectorx.getq()))) { }
      else { result.add(new Double(sectorx.getq())); } }
    return result; }

  public static List getAllOrderedq(List sectors)
  { List result = new Vector();
    for (int i = 0; i < sectors.size(); i++)
    { Sector sectorx = (Sector) sectors.get(i);
      result.add(new Double(sectorx.getq())); } 
    return result; }

  public int getL() { return L; }

  public static List getAllL(List sectors)
  { List result = new Vector();
    for (int i = 0; i < sectors.size(); i++)
    { Sector sectorx = (Sector) sectors.get(i);
      if (result.contains(new Integer(sectorx.getL()))) { }
      else { result.add(new Integer(sectorx.getL())); } }
    return result; }

  public static List getAllOrderedL(List sectors)
  { List result = new Vector();
    for (int i = 0; i < sectors.size(); i++)
    { Sector sectorx = (Sector) sectors.get(i);
      result.add(new Integer(sectorx.getL())); } 
    return result; }

  public double getmu() { return mu; }

  public static List getAllmu(List sectors)
  { List result = new Vector();
    for (int i = 0; i < sectors.size(); i++)
    { Sector sectorx = (Sector) sectors.get(i);
      if (result.contains(new Double(sectorx.getmu()))) { }
      else { result.add(new Double(sectorx.getmu())); } }
    return result; }

  public static List getAllOrderedmu(List sectors)
  { List result = new Vector();
    for (int i = 0; i < sectors.size(); i++)
    { Sector sectorx = (Sector) sectors.get(i);
      result.add(new Double(sectorx.getmu())); } 
    return result; }

  public double nocontagion(int m)
  {   double result = 0;
  Object cached_result = nocontagion_cache.get(new Integer(m));
  if (cached_result != null)
  { result = ((Double) cached_result).doubleValue(); 
    return result; 
  }
  else 
  {   result = Math.pow(( 1 - p ),n - m) * Math.pow(p,m) * Math.pow(( 1 - q ),m * ( n - m ));

    nocontagion_cache.put(new Integer(m), new Double(result));
  }
  return result;
 }

  public double contagion(int i,int m)
  {   double result = 0;
 
  result = Math.pow(( 1 - p ),n - i) * Math.pow(p,i) * Math.pow(( 1 - q ),i * ( n - m )) * Math.pow(( 1 - Math.pow(( 1 - q ),i) ),m - i) * StatFunc.comb(m,i);
    return result;
  }

  private  java.util.Map nocontagion_cache = new java.util.HashMap();

}

class Borrower
{
  private String name = "";

  private double L = 0;  // loss of default
  private double p = 0;  // probability of default

  private double rc = 0;

  private List borrowerInSectors = new Vector();

  public Borrower() {}

  public Borrower(Borrower borrower) {
      this.name = borrower.name;
      this.L = borrower.L;
      this.p = borrower.p;
      borrowerInSectors = borrower.borrowerInSectors;
  }

  public Borrower(double L, double p) {
    this.L = L;
    this.p = p;
    borrowerInSectors = new Vector();
  }

  public void setname(String name) {
    this.name = name;
  }

  public void setL(double L) {this.L = L;}

  public void setp(double p) {this.p = p;}

  public void setrc(double rc) {this.rc = rc;}

  public String getname() {return name;}

  public double getL() {
    return L;
  }

  public double getP() {
    return p;
  }

  public double getrc() {return rc;}

  public void addborrowerInSectors(Borrower borrowerInSector) {
    borrowerInSectors.add(borrowerInSector);
  }

  public List getBorrowerInSectors() {
      return borrowerInSectors;
  }

  public List getSectors() {
      List<Sector> sectors = new Vector<>();
      for (Object borrowerInSector : borrowerInSectors) {
          sectors.add(((BorrowerInSector) borrowerInSector).getSector());
      }
      return sectors;
  }

  public BorrowerInSector getBorrowerInSectorFrom(Sector sector) {
      for (Object borrowerInSector : borrowerInSectors) {
          if (((BorrowerInSector) borrowerInSector).getSector().equals(sector))
              return ((BorrowerInSector) borrowerInSector);
      }
      return null;
  }

  public boolean equals(Borrower borrower) {
        return (name == borrower.name);
    }

  public String toString()
  { String _res_ = "(Borrower) ";
    _res_ = _res_ + name + ",";
    _res_ = _res_ + L + ",";
    _res_ = _res_ + p + ",";
    return _res_;
  }
}

class BorrowerInSector extends Borrower
{
  private double omega = 0;  // weighting of loss
  private double theta = 0;  //

  private Borrower borrower = null;
  private Sector sector = null;

  public BorrowerInSector() {}

  public BorrowerInSector(double omega, double theta, Borrower borrower, Sector sector) {
    super(borrower);
    this.omega = omega;
    this.theta = theta;
    this.sector = sector;
  }

  public void setomega(double omega) {
    this.omega = omega;
  }

  public void settheta(double theta) {
    this.theta = theta;
  }

  public void setborrower(Borrower borrower) {
    this.borrower = borrower;
  }

  public double getOmega() {
    return omega;
  }

  public double getTheta() {
    return theta;
  }

  public Sector getSector() {
      return sector;
  }

  public Borrower getBorrower() {return borrower;}

  // omega*theta*L
  public double collect1() {
      return omega*theta*getL();
  }

  // (omega*theta)^2*L^2
  public double collect2() {
      return Math.pow(collect1(),2);
  }

  public String toString()
  { String _res_ = "(BorrowerInSector) ";
    _res_ = _res_ + getname() + ",";
    _res_ = _res_ + getL() + ",";
    _res_ = _res_ + getP() + ",";
    _res_ = _res_ + omega + ",";
    _res_ = _res_ + theta + ",";
    return _res_;
  }
}

class StatFunc
  implements SystemTypes
{

  public StatFunc()
  {

  }



  public String toString()
  { String _res_ = "(StatFunc) ";
    return _res_;
  }

  public static int comb(int n,int m)
  {   int result = 0;
    if (n < m || m < 0) { return result; } 
    Object cached_result = comb_cache.get(n + ", " + m);
  if (cached_result != null)
  { result = ((Integer) cached_result).intValue(); 
    return result; 
  }
  else 
  {   if (n - m < m) 
  {   result = Set.prdint(Set.collect_3(Set.integerSubrange(m + 1,n))) / Set.prdint(Set.collect_4(Set.integerSubrange(1,n - m)));
 
  }  else   if (n - m >= m) 
  {   result = Set.prdint(Set.collect_3(Set.integerSubrange(n - m + 1,n))) / Set.prdint(Set.collect_4(Set.integerSubrange(1,m)));
 
  }
    comb_cache.put(n + ", " + m, new Integer(result));
  }
  return result;
 }


  private  static  java.util.Map comb_cache = new java.util.HashMap();

}

public class Controller implements SystemTypes, ControllerInterface
{
  Vector cdos = new Vector();
  Vector sectors = new Vector();
  Vector borrowers = new Vector();
  Vector borrowerInSectors = new Vector();
  Vector statfuncs = new Vector();
  private static Controller uniqueInstance; 


  private Controller() { } 


  public static Controller inst() 
    { if (uniqueInstance == null) 
    { uniqueInstance = new Controller(); }
    return uniqueInstance; }

  // Todo: complete
  public static void computeLPinSector() {

  }

  // Todo: test
  public static void linkBorrowerWithSector() {
    Vector borrowerInSectors = uniqueInstance.borrowerInSectors;
    for (Object borrowerInSector:borrowerInSectors) {
      Borrower borrower = ((BorrowerInSector) borrowerInSector).getBorrower();
      ((BorrowerInSector) borrowerInSector).setname(borrower.getname());
      ((BorrowerInSector) borrowerInSector).setL(borrower.getL());
      ((BorrowerInSector) borrowerInSector).setp(borrower.getP());
    }
  }

  // Todo: print test
  public static void printTest() {
    for (Object cdo:uniqueInstance.cdos) {
      System.out.println(cdo.toString());
    }
    for (Object sector:uniqueInstance.sectors) {
      System.out.println(sector.toString());
    }
    for (Object borrower:uniqueInstance.borrowers) {
      System.out.println(borrower.toString());
    }
    for (Object borrowerInSector:uniqueInstance.borrowerInSectors) {
      System.out.println(borrowerInSector.toString());
    }
  }

  public static void loadModel(String file)
  {
    try
    { BufferedReader br = null;
      File f = new File(file);
      try 
      { br = new BufferedReader(new FileReader(f)); }
      catch (Exception ex) 
      { System.err.println("No file: " + file); return; }
      Class cont = Class.forName("Controller");
      java.util.Map objectmap = new java.util.HashMap();
      while (true)
      { String line1;
        try { line1 = br.readLine(); }
        catch (Exception e)
        { return; }
        if (line1 == null)
        { break; }
        line1 = line1.trim();

        if (line1.length() == 0) { continue; }
        String left;
        String op;
        String right;
        if (line1.charAt(line1.length() - 1) == '"')  // name string
        { int eqind = line1.indexOf("="); 
          if (eqind == -1) { continue; }
          else 
          { left = line1.substring(0,eqind-1).trim();
            op = "="; 
            right = line1.substring(eqind+1,line1.length()).trim();
          }
        }
        else
        { StringTokenizer st1 = new StringTokenizer(line1);
          Vector vals1 = new Vector();
          while (st1.hasMoreTokens())
          { String val1 = st1.nextToken();
            vals1.add(val1);
          }
          if (vals1.size() < 3)
          { continue; }
          left = (String) vals1.get(0);
          op = (String) vals1.get(1);
          right = (String) vals1.get(2);
        }
        if (":".equals(op))
        { int i2 = right.indexOf(".");
          if (i2 == -1)
          { Class cl;
            try { cl = Class.forName("" + right); }
            catch (Exception _x) { System.err.println("No entity: " + right); continue; }
            Object xinst = cl.newInstance();
            objectmap.put(left,xinst);
            Class[] cargs = new Class[] { cl };
            Method addC = cont.getMethod("add" + right,cargs);
            if (addC == null) { continue; }
            Object[] args = new Object[] { xinst };
            addC.invoke(Controller.inst(),args);
          }
          else
          { String obj = right.substring(0,i2);
            String role = right.substring(i2+1,right.length());
            Object objinst = objectmap.get(obj); 
            if (objinst == null) 
            { continue; }
            Object val = objectmap.get(left);
            if (val == null) 
            { continue; }
            Class objC = objinst.getClass();
            Class typeclass = val.getClass(); 
            Object[] args = new Object[] { val }; 
            Class[] settypes = new Class[] { typeclass };
            Method addrole = Controller.findMethod(objC,"add" + role);
            if (addrole != null) 
            { addrole.invoke(objinst, args); }
            else { System.err.println("Error: cannot add to " + role); }
          }
        }
        else if ("=".equals(op))
        { int i1 = left.indexOf(".");
          if (i1 == -1) 
          { continue; }
          String obj = left.substring(0,i1);
          String att = left.substring(i1+1,left.length());
          Object objinst = objectmap.get(obj); 
          if (objinst == null) 
          { continue; }
          Class objC = objinst.getClass();
          Class typeclass; 
          Object val; 
          if (right.charAt(0) == '"' &&
              right.charAt(right.length() - 1) == '"')
          { typeclass = String.class;
            val = right.substring(1,right.length() - 1);
          } 
          else if ("true".equals(right) || "false".equals(right))
          { typeclass = boolean.class;
            if ("true".equals(right))
            { val = new Boolean(true); }
            else
            { val = new Boolean(false); }
          }
          else 
          { val = objectmap.get(right);
            if (val != null)
            { typeclass = val.getClass(); }
            else 
            { int i;
              long l; 
              double d;
              try 
              { i = Integer.parseInt(right);
                typeclass = int.class;
                val = new Integer(i); 
              }
              catch (Exception ee)
              { try 
                { l = Long.parseLong(right);
                  typeclass = long.class;
                  val = new Long(l); 
                }
                catch (Exception eee)
                { try
                  { d = Double.parseDouble(right);
                    typeclass = double.class;
                    val = new Double(d);
                  }
                  catch (Exception ff)
                  { continue; }
                }
              }
            }
          }
          Object[] args = new Object[] { val }; 
          Class[] settypes = new Class[] { typeclass };
          Method setatt = Controller.findMethod(objC,"set" + att);
          if (setatt != null) 
          { setatt.invoke(objinst, args); }
          else { System.err.println("No attribute: " + att); }
        }
      }

      } catch (Exception e) { e.printStackTrace();}

    linkBorrowerWithSector();
  }

  /* Find and return a method named "name" in class named "c" */
  public static Method findMethod(Class c, String name)
  { Method[] mets = c.getMethods(); 
    for (int i = 0; i < mets.length; i++)
    { Method m = mets[i];
      if (m.getName().equals(name))
      { return m; }
    } 
    return null;
  }

  /* Placeholder */
  public void checkCompleteness()
  {   }

  /* Save model in a text file */
  public void saveModel(String file)
  { File outfile = new File(file); 
    PrintWriter out; 
    try { out = new PrintWriter(new BufferedWriter(new FileWriter(outfile))); }
    catch (Exception e) { return; }

    /* Print out ps0 of CDOs*/
    for (int _i = 0; _i < cdos.size(); _i++)
    { CDO cdox_ = (CDO) cdos.get(_i);
      out.println("cdox_" + _i + " : CDO");
      out.println("cdox_" + _i + ".ps0 = " + cdox_.getps0());
    }

    out.println();

    /* Traverse attributes of sectors */
    for (int _i = 0; _i < sectors.size(); _i++)
    { Sector sectorx_ = (Sector) sectors.get(_i);
      out.println("sectorx_" + _i + " : Sector");
      out.println("sectorx_" + _i + ".name = \"" + sectorx_.getname() + "\"");
      out.println("sectorx_" + _i + ".n = " + sectorx_.getn());
      out.println("sectorx_" + _i + ".p = " + sectorx_.getp());
      out.println("sectorx_" + _i + ".q = " + sectorx_.getq());
      out.println("sectorx_" + _i + ".L = " + sectorx_.getL());
      out.println("sectorx_" + _i + ".mu = " + sectorx_.getmu());
    }

    out.println();

    for (int _i = 0; _i < borrowers.size(); _i++) {
      Borrower borrowerx_ = (Borrower) borrowers.get(_i);
      out.println("borrowerx_" + _i + " : Borrower");
      out.println("borrowerx_" + _i + ".name = \"" + borrowerx_.getname() + "\"");
      out.println("borrowerx_" + _i + ".L = " + borrowerx_.getL());
      out.println("borrowerx_" + _i + ".p = " + borrowerx_.getP());
      out.println("borrowerx_" + _i + ".risk_contribution = " + borrowerx_.getrc());
    }

    out.println();

    for (int _i = 0; _i < borrowerInSectors.size(); _i++) {
      BorrowerInSector borrowerInSectorx_ = (BorrowerInSector) borrowerInSectors.get(_i);
      out.println("borrowerInSectorx_" + _i + " : BorrowerInSector");
      out.println("borrowerInSectorx_" + _i + ".theta = " + borrowerInSectorx_.getTheta() + "");
      out.println("borrowerInSectorx_" + _i + ".omega = " + borrowerInSectorx_.getOmega() + "");
      out.println("borrowerInSectorx_" + _i + ".borrower = " + borrowerInSectorx_.getBorrower().getname() + "");
    }

    /* Printing out statistics data of CDOs */
    for (int _i = 0; _i < statfuncs.size(); _i++)
    { StatFunc statfuncx_ = (StatFunc) statfuncs.get(_i);
      out.println("statfuncx_" + _i + " : StatFunc");
    }

    out.println();

    for (int _i = 0; _i < borrowers.size(); _i++)
    { Borrower borrower = (Borrower) borrowers.get(_i);
      List borrower_borrowerInSectors = borrower.getBorrowerInSectors();
      for (int _j = 0; _j < borrower_borrowerInSectors.size(); _j++)
      { out.println("borrowerInSectorx_" + borrowerInSectors.indexOf(borrower_borrowerInSectors.get(_j)) + " : borrowerx_" + _i + ".borrowerInSectors");
      }
    }

    out.println();

    for (int _i = 0; _i < sectors.size(); _i++)
    { Sector sectorx_ = (Sector) sectors.get(_i);
      List sector_borrowers = sectorx_.getBorrowerInSectors();
      for (int _j = 0; _j < sector_borrowers.size(); _j++)
      { out.println("borrowerInSectorx_" + borrowerInSectors.indexOf(sector_borrowers.get(_j)) + " : sectorx_" + _i + ".borrowerInSectors");
      }
    }

    out.println();

    for (int _i = 0; _i < cdos.size(); _i++)
    { CDO cdox_ = (CDO) cdos.get(_i);
      List cdo_sectors_Sector = cdox_.getsectors();
      for (int _j = 0; _j < cdo_sectors_Sector.size(); _j++)
      { out.println("sectorx_" + sectors.indexOf(cdo_sectors_Sector.get(_j)) + " : cdox_" + _i + ".sectors");
      }
    }

    out.close();
  }

  /* Save model in xsi format */
  public void saveXSI(String file)
  { File outfile = new File(file); 
    PrintWriter out; 
    try { out = new PrintWriter(new BufferedWriter(new FileWriter(outfile))); }
    catch (Exception e) { return; } 
    out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    out.println("<My:model xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\">");
    for (int _i = 0; _i < cdos.size(); _i++)
    { CDO cdox_ = (CDO) cdos.get(_i);
       out.print("<cdos xsi:type=\"My:CDO\"");
    out.print(" ps0=\"" + cdox_.getps0() + "\" ");
    out.print(" sectors = \"");
    List cdo_sectors = cdox_.getsectors();
    for (int _j = 0; _j < cdo_sectors.size(); _j++)
    { out.print(" //@sectors." + sectors.indexOf(cdo_sectors.get(_j)));
    }
    out.print("\"");
    out.println(" />");
  }

    /* Traverse attributes of sectors */
    for (int _i = 0; _i < sectors.size(); _i++)
    { Sector sectorx_ = (Sector) sectors.get(_i);
       out.print("<sectors xsi:type=\"My:Sector\"");
    out.print(" name=\"" + sectorx_.getname() + "\" ");
    out.print(" n=\"" + sectorx_.getn() + "\" ");
    out.print(" p=\"" + sectorx_.getp() + "\" ");
    out.print(" q=\"" + sectorx_.getq() + "\" ");
    out.print(" L=\"" + sectorx_.getL() + "\" ");
    out.print(" mu=\"" + sectorx_.getmu() + "\" ");
    out.println(" />");
  }

    /* Could be printing out statistics data of CDOs */ //Todo: implement this block
    for (int _i = 0; _i < statfuncs.size(); _i++)
    { StatFunc statfuncx_ = (StatFunc) statfuncs.get(_i);
       out.print("<statfuncs xsi:type=\"My:StatFunc\"");
    out.println(" />");
  }

    out.println("</My:model>");
    out.close(); 
  }

  /* Add a new CDO to CDO list */
  public void addCDO(CDO oo) { cdos.add(oo); }

  /* Add a new sector to sector list */
  public void addSector(Sector oo) { sectors.add(oo); }

  /* Add a new borrower to borrower list */
  public void addBorrower(Borrower borrower) {
    borrowers.add(borrower);
  }

  /* Add a new borrowerInSector to borrowerInSector list */
  public void addBorrowerInSector(BorrowerInSector borrowerInSector) {
    borrowerInSectors.add(borrowerInSector);
  }

  /* Add a *(statistical function) to StatFunc list */
  public void addStatFunc(StatFunc oo) { statfuncs.add(oo); }

  /* Add all CDOs from input to existing CDO list */
  public void createAllCDO(List cdox)
  { for (int i = 0; i < cdox.size(); i++)
    { CDO cdox_x = (CDO) cdox.get(i);
      if (cdox_x == null) { cdox_x = new CDO(); }
      cdox.set(i,cdox_x);
      addCDO(cdox_x);
    }
  }

  /* Initialise a new CDO and add it into existing CDO list */
  public CDO createCDO()
  { 
    CDO cdox = new CDO();
    addCDO(cdox);
    setps0(cdox,0);
    setsectors(cdox,new Vector());

    return cdox;
  }

  /* Add all sectors from input to existing sector list */
  public void createAllSector(List sectorx)
  { for (int i = 0; i < sectorx.size(); i++)
    { Sector sectorx_x = (Sector) sectorx.get(i);
      if (sectorx_x == null) { sectorx_x = new Sector(); }
      sectorx.set(i,sectorx_x);
      addSector(sectorx_x);
    }
  }

  /* Initialise a new sector and add it into existing sector list */
  public Sector createSector()
  { 
    Sector sectorx = new Sector();
    addSector(sectorx);
    setname(sectorx,"");
    setn(sectorx,0);
    setp(sectorx,0);
    setq(sectorx,0);
    setL(sectorx,0);
    setmu(sectorx,0);

    return sectorx;
  }

  /* Add all *(statistical functions) from input to existing statfunc list *///Todo: what is statfunc
  public void createAllStatFunc(List statfuncx)
  { for (int i = 0; i < statfuncx.size(); i++)
    { StatFunc statfuncx_x = (StatFunc) statfuncx.get(i);
      if (statfuncx_x == null) { statfuncx_x = new StatFunc(); }
      statfuncx.set(i,statfuncx_x);
      addStatFunc(statfuncx_x);
    }
  }

  /* Initialise a new *(statistical functions) and add it into existing statfunc list *///Todo: what is statfunc
  public StatFunc createStatFunc()
  { 
    StatFunc statfuncx = new StatFunc();
    addStatFunc(statfuncx);

    return statfuncx;
  }

  /* Set ps0 of a CDO as specified parameter */
  public void setps0(CDO cdox, double ps0_x)
  { cdox.setps0(ps0_x);
    }

  /* Set sectors of a CDO as specified sector list */
  public void setsectors(CDO cdox, List sectorsxx) 
  {   List _oldsectorsxx = cdox.getsectors();
  for (int _i = 0; _i < sectorsxx.size(); _i++)
  { Sector _xx = (Sector) sectorsxx.get(_i);
    if (_oldsectorsxx.contains(_xx)) { }
    else { CDO.removeAllsectors(cdos, _xx); }
  }
    cdox.setsectors(sectorsxx);
      }

  /* Set sectors[_ind] of a CDO as specified sector */
  public void setsectors(CDO cdox, int _ind, Sector sectorx) 
  { cdox.setsectors(_ind,sectorx); }

  /* Add a sector to a CDO */
  public void addsectors(CDO cdox, Sector sectorsxx) 
  {   CDO.removeAllsectors(cdos,sectorsxx); //Remove a sector from all CDOs that contain it
    cdox.addsectors(sectorsxx);
    }

  /* Remove a sector from existing sector list of a CDO*/
  public void removesectors(CDO cdox, Sector sectorsxx) 
  { cdox.removesectors(sectorsxx);
    }

  /* Add a list of sectors to a CDO */
  public void unionsectors(CDO cdox,List sectorsx)
  { for (int _i = 0; _i < sectorsx.size(); _i++)
    { Sector sectorxsectors = (Sector) sectorsx.get(_i);
      addsectors(cdox,sectorxsectors);
     } }

  public void subtractsectors(CDO cdox,List sectorsx)
  { for (int _i = 0; _i < sectorsx.size(); _i++)
    { Sector sectorxsectors = (Sector) sectorsx.get(_i);
      removesectors(cdox,sectorxsectors);
     } } 


  public void setname(Sector sectorx, String name_x)
  { sectorx.setname(name_x);
    }


  public void setn(Sector sectorx, int n_x)
  { sectorx.setn(n_x);
    }

  public void setp(Sector sectorx, double p_x)
  { sectorx.setp(p_x);
    }


  public void setq(Sector sectorx, double q_x)
  { sectorx.setq(q_x);
    }


  public void setL(Sector sectorx, int L_x)
  { sectorx.setL(L_x);
    }


  public void setmu(Sector sectorx, double mu_x)
  { sectorx.setmu(mu_x);
    }



  public  List AllCDOnocontagion(List cdoxs,int k,int m)
  { 
    List result = new Vector();
    for (int _i = 0; _i < cdoxs.size(); _i++)
    { CDO cdox = (CDO) cdoxs.get(_i);
      result.add(new Double(cdox.nocontagion(k, m)));
    }
    return result; 
  }

  public  List AllCDOP(List cdoxs,int k,int m)
  { 
    List result = new Vector();
    for (int _i = 0; _i < cdoxs.size(); _i++)
    { CDO cdox = (CDO) cdoxs.get(_i);
      result.add(new Double(cdox.P(k, m)));
    }
    return result; 
  }

  public  List AllCDOPCond(List cdoxs,int k,int m)
  { 
    List result = new Vector();
    for (int _i = 0; _i < cdoxs.size(); _i++)
    { CDO cdox = (CDO) cdoxs.get(_i);
      result.add(new Double(cdox.PCond(k, m)));
    }
    return result; 
  }

  public  List AllCDOmaxfails(List cdoxs,int k,int s)
  { 
    List result = new Vector();
    for (int _i = 0; _i < cdoxs.size(); _i++)
    { CDO cdox = (CDO) cdoxs.get(_i);
      result.add(new Integer(cdox.maxfails(k, s)));
    }
    return result; 
  }

  public  List AllCDOPS(List cdoxs,int s)
  { 
    List result = new Vector();
    for (int _i = 0; _i < cdoxs.size(); _i++)
    { CDO cdox = (CDO) cdoxs.get(_i);
      result.add(new Double(cdox.PS(s)));
    }
    return result; 
  }

  public  List AllCDOVS(List cdoxs,int k,int s)
  { 
    List result = new Vector();
    for (int _i = 0; _i < cdoxs.size(); _i++)
    { CDO cdox = (CDO) cdoxs.get(_i);
      result.add(new Double(cdox.VS(k, s)));
    }
    return result; 
  }

  public void test1(CDO cdox,Sector s)
  {   cdox.test1(s);
   }

  public void test1outer(CDO cdox)
  {   cdox.test1outer();
   }

  public void test2(CDO cdox)
  {   cdox.test2();
   }

  public void test3(CDO cdox)
  {   cdox.test3();
   }

  public  List AllSectornocontagion(List sectorxs,int m)
  { 
    List result = new Vector();
    for (int _i = 0; _i < sectorxs.size(); _i++)
    { Sector sectorx = (Sector) sectorxs.get(_i);
      result.add(new Double(sectorx.nocontagion(m)));
    }
    return result; 
  }

  public  List AllSectorcontagion(List sectorxs,int i,int m)
  { 
    List result = new Vector();
    for (int _i = 0; _i < sectorxs.size(); _i++)
    { Sector sectorx = (Sector) sectorxs.get(_i);
      result.add(new Double(sectorx.contagion(i, m)));
    }
    return result; 
  }

 public static int comb(int n,int m)
 { return StatFunc.comb(n, m); }



  public void killAllCDO(List cdoxx)
  { for (int _i = 0; _i < cdoxx.size(); _i++)
    { killCDO((CDO) cdoxx.get(_i)); }
  }

  public void killCDO(CDO cdoxx)
  { cdos.remove(cdoxx);
  }



  public void killAllSector(List sectorxx)
  { for (int _i = 0; _i < sectorxx.size(); _i++)
    { killSector((Sector) sectorxx.get(_i)); }
  }

  public void killSector(Sector sectorxx)
  { sectors.remove(sectorxx);
    Vector _1qrangesectorsCDO = new Vector();
    _1qrangesectorsCDO.addAll(cdos);
    for (int _i = 0; _i < _1qrangesectorsCDO.size(); _i++)
    { CDO cdox = (CDO) _1qrangesectorsCDO.get(_i);
      if (cdox.getsectors().contains(sectorxx))
      { removesectors(cdox,sectorxx); }
    }
  }



  public void killAllStatFunc(List statfuncxx)
  { for (int _i = 0; _i < statfuncxx.size(); _i++)
    { killStatFunc((StatFunc) statfuncxx.get(_i)); }
  }

  public void killStatFunc(StatFunc statfuncxx)
  { statfuncs.remove(statfuncxx);
  }




  
    public void test() 
  {    Date d1 = new Date();
       long t1 = d1.getTime(); 


       List cdotest1outerx = new Vector();
  cdotest1outerx.addAll(Controller.inst().cdos);
  for (int cdotest1outerx_ind4 = 0; cdotest1outerx_ind4 < cdotest1outerx.size(); cdotest1outerx_ind4++)
  { Controller.inst().test1outer((CDO) cdotest1outerx.get(cdotest1outerx_ind4)); }

       List cdotest2x = new Vector();
  cdotest2x.addAll(Controller.inst().cdos);
  for (int cdotest2x_ind5 = 0; cdotest2x_ind5 < cdotest2x.size(); cdotest2x_ind5++)
  { Controller.inst().test2((CDO) cdotest2x.get(cdotest2x_ind5)); }

       List cdotest3x = new Vector();
  cdotest3x.addAll(Controller.inst().cdos);
  for (int cdotest3x_ind6 = 0; cdotest3x_ind6 < cdotest3x.size(); cdotest3x_ind6++)
  { Controller.inst().test3((CDO) cdotest3x.get(cdotest3x_ind6)); }

   Date d2 = new Date();
   long t2 = d2.getTime(); 
   System.out.println("Time = " + (t2-t1)); 

  }


 
}



