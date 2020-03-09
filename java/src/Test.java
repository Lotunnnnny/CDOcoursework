public class Test {
    public static void main(String[] args) {

        Borrower a = new Borrower(2,0.3);
        Borrower b = new Borrower(4,0.2);
        Borrower c = new Borrower(1,0.1);

        Sector i = new Sector();
        Sector j = new Sector();

        BorrowerInSector aIni = new BorrowerInSector(0.5,1,a,i);
        BorrowerInSector bIni = new BorrowerInSector(0.5,0.7,b,i);
        BorrowerInSector bInj = new BorrowerInSector(0.4,0.3,b,j);
        BorrowerInSector cInj = new BorrowerInSector(0.6,1,c,j);

        CDO cdo_1 = new CDO();

        i.add(aIni);
        i.add(bIni);
        j.add(bInj);
        j.add(cInj);

        cdo_1.add(i);
        cdo_1.add(j);


        
        System.out.println(cdo_1.getSigma());




    }
}
