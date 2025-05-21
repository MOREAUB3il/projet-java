package personnage;

import java.util.ArrayList;

public abstract class Support extends Personnage {
	
private int soin;
    
    public int getSoin() {
        return soin;
    }
    public void setSoin(int soin) {
        if (soin >= 0) {
            this.soin = soin;
        }
    }
    public Support(String nom, String support) {
    	super(nom, support);
        this.setSante(getSante());
        this.soin = 10;
    }
    @Override
    public void augmenterStats() {
    	setPvMax(getPvMax() + 5);
        setPv(getPv() + 5);
        setForceBase(getForce() + 1);
        soin += 2;
        System.out.println(getNom() + " gagne +5 PV, +1 Force, +2 Soin !");
    }
    @Override
    public void subirDegats(int degats) {
    	int degatsReduits = degats * (100 - soin) / 100;
    	setPv(getPv() - degatsReduits);
    	if (getPv() < 0) setPv(0);
        System.out.println(getNom() + " perd " + degats + " PV. Il lui reste " + getPv() + " PV.");
    }
    
    public void soigner(Personnage cible) {
        int montant = (getSoin() * cible.getPvMax()) / 100;
        cible.setPv(Math.min(cible.getPv() + montant, cible.getPvMax()));

        System.out.println(getNom() + " 💖 soigne " + cible.getNom() + " de " + montant + " PV !");
        System.out.println(cible.getNom() + " a maintenant " + cible.getPv() + "/" + cible.getPvMax() + " PV.");
    }

    public void soinCibleFaible(ArrayList<Personnage> equipe) {
        Personnage plusBlesse = null;
        int pvMin = Integer.MAX_VALUE;

        for (Personnage p : equipe) {
            if (p == this) continue;
            if (p.getPv() < pvMin) {
                pvMin = p.getPv();
                plusBlesse = p;
            }
        }

        if (plusBlesse != null) {
            int montantSoin = (getSoin() * plusBlesse.getPvMax()) / 100;
            plusBlesse.setPv(Math.min(plusBlesse.getPv() + montantSoin, plusBlesse.getPvMax()));

            System.out.println(getNom() + " 💖 soigne " + plusBlesse.getNom() + " de " + montantSoin + " PV !");
            System.out.println(plusBlesse.getNom() + " a maintenant " + plusBlesse.getPv() + "/" + plusBlesse.getPvMax() + " PV.");
        } else {
            System.out.println(getNom() + " n'a trouvé personne à soigner.");
        }
    }
}
