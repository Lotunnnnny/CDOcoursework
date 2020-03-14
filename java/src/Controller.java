import java.util.*;

import java.lang.*;
import java.lang.reflect.*;
import java.io.*;

class CDO
  implements SystemTypes
{
    private double ps0 = 0; // internal
    private static List sectors = new Vector(); // of Sector

    private double sigma = 0;
    private double variance = 0;
    private double expectedLoss = 0;

    private Map PS_cache = new HashMap();
    private Map PS_result = new LinkedHashMap();

    public CDO()
    {
        this.ps0 = 0;                         //probability of ZERO default
    }

    /* Description of CDO */
    public String toString()
    { String _res_ = "(CDO) ";
        _res_ = _res_ + ps0;
        return _res_;
    }

    /*------------------------------------------set/get/add/remove/union----------------------------------------------*/

    public static void addAllsectors(List cdos,Sector _val)
    { for (int _i = 0; _i < cdos.size(); _i++)
    { CDO cdox = (CDO) cdos.get(_i);
        Controller.inst().addsectors(cdox, _val); } }                                       //set all sectors

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

    public static void setAllsectors(List cdos,List _val)
    { for (int _i = 0; _i < cdos.size(); _i++)
    { CDO cdox = (CDO) cdos.get(_i);
        Controller.inst().setsectors(cdox, _val); } }                                      //set all sectors

    public static void setAllsectors(List cdos,int _ind,Sector _val)
    { for (int _i = 0; _i < cdos.size(); _i++)
    { CDO cdox = (CDO) cdos.get(_i);
        Controller.inst().setsectors(cdox,_ind,_val); } }                                   //set all sectors

    public Map getPS_result() {
        return PS_result;
    }

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

    public void removesectors(Sector sectorsxx) { Vector _removedsectorssectorsxx = new Vector();
        _removedsectorssectorsxx.add(sectorsxx);
        sectors.removeAll(_removedsectorssectorsxx);
    }                                                                                      //remove sector

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
    /*------------------------------------------set/get/add/remove/union----------------------------------------------*/



    /*------------------------------------------------computation-----------------------------------------------------*/

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

    public int maxfails(int k, double s)
    {   int result = 0;

      int num =  (int)Math.floor(s / ((Sector) sectors.get(k - 1)).getL());
      if (((Sector) sectors.get(k - 1)).getn() <= num)
      {   result = ((Sector) sectors.get(k - 1)).getn();

      }  else   if (((Sector) sectors.get(k - 1)).getn() > num)
      {   result = num;

      }
      return result;
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
      {   result = Arith.div(Set.sumdouble(Set.collect_1(Set.integerSubrange(1,sectors.size()),this,s)), s);

      }
        PS_cache.put(s, new Double(result));
      }
      return result;
    }

    public double VS(int k, double s)
    {
        double result = 0;
        if (s > 0)
            result = Set.sumdouble(Set.collect_2(Set.integerSubrange(1,this.maxfails(k,s)),this,k,s));
        return result;
    }

    public void calculateStat() {
        calculateExpectedLoss();
        calculateVariance();
        calculateSigma();
    }

    private void calculateExpectedLoss() {
        double result = 0;
        for (int k = 1; k <= sectors.size(); k++) {
            Sector sector = (Sector) sectors.get(k-1);
            double sum = 0;
            for (int m = 1; m <= sector.getn(); m++) {
                sum += m*P(k,m);
            }
            result += sector.getL()*sum;
        }
        expectedLoss = result;
    }

    private void calculateSigma() {
        sigma = Math.sqrt(variance);
    }

    private void calculateVariance() {
        double result = 0;
        for (int k = 1; k <= sectors.size(); k++) {
            Sector s = (Sector) sectors.get(k-1);
            for (int m = 1; m <= s.getn(); m++) {
                result += m * m * s.getL() * s.getL() * P(k,m) ;
            }
        }
        variance = result;
    }

    public void riskContribution(int k) {
        Sector s = (Sector) sectors.get(k-1);
        double result = 0;
        for (int m = 1; m <= s.getn(); m++) {
            result = Arith.add(Arith.mul(Arith.mul(Arith.pow(m,2), Arith.pow(s.getL(),2)), P(k,m)), result);
        }
        s.setRC(Arith.div(result,sigma));
    }

    public void borrowerRiskContribution(int k, int i) {
        Sector sector = (Sector) sectors.get(k-1);
        BorrowerInSector bis = (BorrowerInSector)sector.getBorrowerInSectors().get(i-1);
        double result = 0;
        for (int m = 1; m <= sector.getn(); m++) {
            result = Arith.add(Arith.mul(Arith.mul(sector.getL(), Arith.pow(m,2)), P(k,m)), result);
        }
        bis.setrc(Arith.mul(Arith.div(result,sigma), Arith.div(bis.getL(), bis.getTheta())));
    }

    /*------------------------------------------------computation-----------------------------------------------------*/



    /*----------------------------------------------------test--------------------------------------------------------*/

    public void test1(Sector s)
    { Controller.inst().calculateSector(s);
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

    public double getMaxLoss() {
        double max = 0;
        for (int _k = 1; _k <= sectors.size(); _k++) {
            Sector s = (Sector)sectors.get(_k-1);
            max += s.getn()*s.getL();
        }
        return 50;
    }

    public void test3()
    {     List _double_list2 = new Vector();
        _double_list2.addAll(Set.doubleSubrange(0,getMaxLoss(),0.01));
        for (int _ind3 = 0; _ind3 < _double_list2.size(); _ind3++)
        {
            double s = (double) _double_list2.get(_ind3);
            PS_result.put(s,this.PS(s));
        }
    }

    public void test4() {
        calculateStat();
        for (int k = 1; k <= getsectors().size(); k++){
            riskContribution(k);
            Sector sector = (Sector) getsectors().get(k-1);
            for (int i = 1; i <= sector.getn(); i++) {
                borrowerRiskContribution(k,i);
            }
        }
    }

    /*----------------------------------------------------test--------------------------------------------------------*/
}

class Sector
        implements SystemTypes
{
    private String name = ""; // internal
    private int n = 0; // internal
    private double p = 0; // internal
    private double q = 0; // internal
    private double L = 0; // internal
    private double mu = 0; // internal

    private double rc = 0;

    private List borrowerInSectors = new Vector();
    private CDO cdo = null;

    private  java.util.Map nocontagion_cache = new java.util.HashMap();

    public Sector()
    {
    this.name = "";
    this.n = 0;
    this.p = 0;
    this.q = 0;
    this.L = 0;
    this.mu = 0;
    }

    public Sector(String name, List borrowerInSectors) {
        this.name = name;
        this.borrowerInSectors = borrowerInSectors;
        this.n = borrowerInSectors.size();
    }

    /* Description of Sector */
    public String toString() {
      String _res_ = "(Sector) ";
        _res_ = _res_ + name + ",";
        _res_ = _res_ + n + ",";
        _res_ = _res_ + p + ",";
        _res_ = _res_ + q + ",";
        _res_ = _res_ + L + ",";
        _res_ = _res_ + mu;
        return _res_;
    }



    /*------------------------------------------set/get/add/remove/union----------------------------------------------*/

    public void addborrowerInSectors(BorrowerInSector borrowerInSector) {
        borrowerInSectors.add(borrowerInSector);
    }

    public void setBorrowerInSectors(List borrowerInSectors) {
        this.borrowerInSectors = borrowerInSectors;
    }

    public void setCdo(CDO cdo) {
        this.cdo = cdo;
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

    public void setL(double L_x) { L = L_x;  }

    public static void setAllL(List sectors,int val)
    { for (int i = 0; i < sectors.size(); i++)
    { Sector sectorx = (Sector) sectors.get(i);
      Controller.inst().setL(sectorx,val); } }

    public void setmu(double mu_x) { mu = mu_x;  }

    public static void setAllmu(List sectors,double val)
    { for (int i = 0; i < sectors.size(); i++)
    { Sector sectorx = (Sector) sectors.get(i);
      Controller.inst().setmu(sectorx,val); } }

    public void setRC(double rc) {
        this.rc = rc;
    }

    public double getRC() {
        return rc;
    }

    public List getBorrowerInSectors() {
        return borrowerInSectors;
    }

    public CDO getCdo() {
        return cdo;
    }

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

    public double getL() { return L; }

    public static List getAllL(List sectors)
    { List result = new Vector();
    for (int i = 0; i < sectors.size(); i++)
    { Sector sectorx = (Sector) sectors.get(i);
      if (result.contains(sectorx.getL())) { }
      else { result.add(sectorx.getL()); } }
    return result; }

    public static List getAllOrderedL(List sectors)
    { List result = new Vector();
    for (int i = 0; i < sectors.size(); i++)
    { Sector sectorx = (Sector) sectors.get(i);
      result.add(sectorx.getL()); }
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

    /*------------------------------------------set/get/add/remove/union----------------------------------------------*/



    /*------------------------------------------------computation-----------------------------------------------------*/

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

    public void calculateSector() {
        calculatePfromBorrowers();
        calculateLfromBorrowers();
        calculateMufromBorrowers();
    }

    private void calculatePfromBorrowers() {
        p = 0;
        for (int _borrowerInSectors=0; _borrowerInSectors<n; _borrowerInSectors++) {
            BorrowerInSector borrowerInSector = ((BorrowerInSector)borrowerInSectors.get(_borrowerInSectors));
            p = Arith.add(p,borrowerInSector.getP());
        }
    }

    private void calculateLfromBorrowers() {
        L = 0;
        for (int _borrowerInSectors=0; _borrowerInSectors<n; _borrowerInSectors++) {
            BorrowerInSector borrowerInSector = ((BorrowerInSector)borrowerInSectors.get(_borrowerInSectors));
            L = Arith.add(L,borrowerInSector.getL());
        }
    }

    private void calculateMufromBorrowers() {
//        mu = 1 - Math.pow(1-p, n);
        mu = Arith.sub(1, Arith.pow(1-p, n));
    }

    /*------------------------------------------------computation-----------------------------------------------------*/
}

class Borrower
{
    protected String name = "";

    protected double L = 0;  // loss of default
    protected double p = 0;  // probability of default

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

    /* Description of Borrower */
    public String toString()
    { String _res_ = "(Borrower) ";
        _res_ = _res_ + name + ",";
        _res_ = _res_ + L + ",";
        _res_ = _res_ + p;
        return _res_;
    }




    /*-------------------------------------------------set/get/add----------------------------------------------------*/

    public void addborrowerInSectors(Borrower borrowerInSector) {
        borrowerInSectors.add(borrowerInSector);
    }

    public boolean romoveborrowerInSectors(Borrower borrowerInSector) {
        return borrowerInSectors.remove(borrowerInSector);
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

    /*-------------------------------------------------set/get/add----------------------------------------------------*/

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

    /* Description of BorrowerInSector */
    public String toString()
    { String _res_ = "(BorrowerInSector) ";
        _res_ = _res_ + name + ",";
        _res_ = _res_ + L + ",";
        _res_ = _res_ + p + ",";
        _res_ = _res_ + omega + ",";
        _res_ = _res_ + theta;
        return _res_;
    }



    /*-------------------------------------------------set/get/add----------------------------------------------------*/

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

    /*-------------------------------------------------set/get/add----------------------------------------------------*/



    /*-------------------------------------------------computation----------------------------------------------------*/

    // omega*theta*L
    public void calculateL() {
      L = Arith.mul(Arith.mul(omega,theta),borrower.getL());
    }

    // omega*theta*p
    public void calculateP() {
        p = Arith.mul(Arith.mul(omega,theta),borrower.getP());
    }

    /*-------------------------------------------------computation----------------------------------------------------*/
}

class StatFunc
        implements SystemTypes
{
    private  static  java.util.Map comb_cache = new java.util.HashMap();

    public StatFunc() {}

    /* Description of StatFunc */
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
}

public class Controller
        implements SystemTypes, ControllerInterface
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

    public static void linkBorrowerWithSector() {
    Vector borrowerInSectors = uniqueInstance.borrowerInSectors;
    for (Object borrowerInSector:borrowerInSectors) {
      BorrowerInSector borrowerInSector1 = (BorrowerInSector) borrowerInSector;
      Borrower borrower = borrowerInSector1.getBorrower();
      borrowerInSector1.setname(borrower.getname());
      ((BorrowerInSector) borrowerInSector).calculateL();
      ((BorrowerInSector) borrowerInSector).calculateP();
    }
    }

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
    }

    out.println();

    for (int _i = 0; _i < borrowerInSectors.size(); _i++) {
      BorrowerInSector borrowerInSectorx_ = (BorrowerInSector) borrowerInSectors.get(_i);
      out.println("borrowerInSectorx_" + _i + " : BorrowerInSector");
      out.println("borrowerInSectorx_" + _i + ".theta = " + borrowerInSectorx_.getTheta() + "");
      out.println("borrowerInSectorx_" + _i + ".omega = " + borrowerInSectorx_.getOmega() + "");
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

    out.println();

    for (int _i = 0; _i < cdos.size(); _i++) {
        CDO cdox_ = (CDO) cdos.get(_i);
        Map PS_result = cdox_.getPS_result();
        out.println("cdox_" + _i + ":");
        for (Object entry: PS_result.entrySet()) {
            if ((double)((Map.Entry) entry).getValue() != 0)
                out.println("Probability of loss " + ((Map.Entry) entry).getKey() + " = " + ((Map.Entry) entry).getValue());
        }
        for (Object sector: cdox_.getsectors()) {
            out.println("sectorx_" + sectors.indexOf(sector) + " risk contribution = " + ((Sector)sector).getRC());
            for (Object borrowerInSector: ((Sector)sector).getBorrowerInSectors()) {
                out.println("borrowerInSectorx_" + borrowerInSectors.indexOf(borrowerInSector) + " risk contribution = " + ((BorrowerInSector)borrowerInSector).getrc());
            }
        }
        out.println();
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
      out.print(" borrowerInSectors = \"");
      List sectors_borrowers = sectorx_.getBorrowerInSectors();
      for (int _j = 0; _j < sectors_borrowers.size(); _j++)
      { out.print(" //@borrowerInSectors." + borrowerInSectors.indexOf(sectors_borrowers.get(_j)));
      }
      out.print("\"");
      out.println(" />");
    }

    /* Traverse attributes of borrowers */
    for (int _i = 0; _i < borrowers.size(); _i++)
    { Borrower borrowerx_ = (Borrower) borrowers.get(_i);
      out.print("<borrowers xsi:type=\"My:Borrower\"");
      out.print(" name=\"" + borrowerx_.getname() + "\" ");
      out.print(" L=\"" + borrowerx_.getL() + "\" ");
      out.print(" p=\"" + borrowerx_.getP() + "\" ");
      List borrower_borrowerInSectors = borrowerx_.getBorrowerInSectors();
      for (int _j = 0; _j < borrower_borrowerInSectors.size(); _j++)
      { out.print(" //@borrowerInSectors." + borrowerInSectors.indexOf(borrower_borrowerInSectors.get(_j)));
      }
      out.print("\"");
      out.println(" />");
    }

    /* Traverse attributes of borrowerInSectors */
    for (int _i = 0; _i < borrowerInSectors.size(); _i++)
    { BorrowerInSector borrowerInSectorx_ = (BorrowerInSector) borrowerInSectors.get(_i);
      out.print("<borrowerInSectors xsi:type=\"My:BorrowerInSector\"");
      out.print(" name=\"" + borrowerInSectorx_.getname() + "\" ");
      out.print(" theta=\"" + borrowerInSectorx_.getTheta() + "\" ");
      out.print(" omega=\"" + borrowerInSectorx_.getOmega() + "\" ");
      out.println(" />");
    }


    /* Could be printing out statistics data of CDOs */
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

    /* Add all *(statistical functions) from input to existing statfunc list */
    public void createAllStatFunc(List statfuncx)
    { for (int i = 0; i < statfuncx.size(); i++)
    { StatFunc statfuncx_x = (StatFunc) statfuncx.get(i);
      if (statfuncx_x == null) { statfuncx_x = new StatFunc(); }
      statfuncx.set(i,statfuncx_x);
      addStatFunc(statfuncx_x);
    }
    }

    /* Initialise a new *(statistical functions) and add it into existing statfunc list */
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

    public void calculateSector(Sector sectorx)
    { sectorx.calculateSector();
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

    public void test4(CDO cdo) {
        cdo.test4();
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
    {
        Date d1 = new Date();
        long t1 = d1.getTime();

        List cdotest1outerx = new Vector();
        cdotest1outerx.addAll(Controller.inst().cdos);
        for (int cdotest1outerx_ind4 = 0; cdotest1outerx_ind4 < cdotest1outerx.size(); cdotest1outerx_ind4++)
        { Controller.inst().test1outer((CDO) cdotest1outerx.get(cdotest1outerx_ind4)); }  //set sector

        List cdotest2x = new Vector();
        cdotest2x.addAll(Controller.inst().cdos);
        for (int cdotest2x_ind5 = 0; cdotest2x_ind5 < cdotest2x.size(); cdotest2x_ind5++)
        { Controller.inst().test2((CDO) cdotest2x.get(cdotest2x_ind5)); }  //set ps0

        List cdotest3x = new Vector();
        cdotest3x.addAll(Controller.inst().cdos);
        for (int cdotest3x_ind6 = 0; cdotest3x_ind6 < cdotest3x.size(); cdotest3x_ind6++)
        { Controller.inst().test3((CDO) cdotest3x.get(cdotest3x_ind6)); }  //compute ps all

        List cdotest4x = new Vector();
        cdotest4x.addAll(Controller.inst().cdos);
        for (int i = 0; i < cdotest4x.size(); i++)
        { Controller.inst().test4((CDO) cdotest4x.get(i)); }  //compute rc & brc

        printTest();

        Date d2 = new Date();
        long t2 = d2.getTime();
        System.out.println("Time = " + (t2-t1));
    }
}



