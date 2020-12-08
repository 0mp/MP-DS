package com.mpds.simulator.domain.model.bins;

import com.mpds.simulator.domain.model.Coordinate;
import com.mpds.simulator.domain.model.GridBins;
import com.mpds.simulator.domain.model.Person;

public abstract class TopRight extends Bin{
    protected Bin left;
    protected Bin below;
    protected Bin belowLeft;

    public TopRight(Coordinate ulCorner, Coordinate lrCorner){
        super(ulCorner, lrCorner);
    }

    public void setBelow(Bin bin){
        below = bin;
    }

    public void setBelowLeft(Bin bin){
        belowLeft = bin;
    }

    public void setLeft(Bin bin){left =bin;}

    @Override
    public boolean movePerson(Person currentNode){
        Coordinate pos = currentNode.getPos();
        switch (GridBins.randomGen.nextInt(2)){
            case 0:
                int newRow = pos.getRow() + 1;
                pos.setRow(newRow);
                if (newRow > lrCorner.getRow()){
                    below.toBeAdded.addPerson(currentNode);
                    return true;
                }
                break;
            case 1:
                int newCol = pos.getCol() - 1;
                pos.setCol(newCol);
                if (newCol < ulCorner.getCol()){
                    left.toBeAdded.addPerson(currentNode);
                    return true;
                }
                break;
        }
        return false;
    }
}
