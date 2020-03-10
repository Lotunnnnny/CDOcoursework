public class Test {
    public static void main(String[] args) {

        Borrower a = new Borrower(5,0.02);
        Borrower b = new Borrower(7,0.01);
        Borrower c = new Borrower(4,0.03);

        Sector i = new Sector(0.03);
        Sector j = new Sector(0.05);

        BorrowerInSector aIni = new BorrowerInSector(0.6,1,a,i);
        BorrowerInSector bIni = new BorrowerInSector(0.4,0.7,b,i);
        BorrowerInSector bInj = new BorrowerInSector(0.5,0.3,b,j);
        BorrowerInSector cInj = new BorrowerInSector(0.5,1,c,j);

        CDO cdo_1 = new CDO();

        i.add(aIni);
        i.add(bIni);
        j.add(bInj);
        j.add(cInj);

        cdo_1.add(i);
        cdo_1.add(j);

        System.out.println(cdo_1.getVariance());
        System.out.println(cdo_1.getSigma());
        System.out.println(cdo_1.riskContribution(1));
        System.out.println(cdo_1.riskContribution(2));
        System.out.println(cdo_1.borrowerRiskContribution(1,1));
        System.out.println(cdo_1.borrowerRiskContribution(1,2));
        System.out.println(cdo_1.borrowerRiskContribution(2,1));
        System.out.println(cdo_1.borrowerRiskContribution(2,2));



    }
}
