package fr.istic.vv.report;

public class Report {
    private boolean isMutantAlive;


    public Report(boolean isMutantAlive) {
        super();
        this.isMutantAlive = isMutantAlive;
    }

    public boolean isMutantAlive() {
        return isMutantAlive;
    }

    public void setMutantAlive(boolean isMutantAlive) {
        this.isMutantAlive = isMutantAlive;
    }
/*
    public MutantContainer getMutantContainer() {
        return mutantContainer;
    }

    public void setMutantContainer(MutantContainer mutantContainer) {
        this.mutantContainer = mutantContainer;
    }*/
}
