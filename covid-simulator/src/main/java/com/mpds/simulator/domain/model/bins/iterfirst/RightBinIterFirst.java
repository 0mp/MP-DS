package com.mpds.simulator.domain.model.bins.iterfirst;

import com.mpds.simulator.domain.model.Coordinate;
import com.mpds.simulator.domain.model.Person;
import com.mpds.simulator.domain.model.bins.Bin;
import com.mpds.simulator.domain.model.bins.RightBin;

public class RightBinIterFirst extends RightBin {

    public RightBinIterFirst(Coordinate ulCorner, Coordinate lrCorner){
        super(ulCorner, lrCorner);
    }

    @Override
    public void findInteractionsWithNeighbours(Person person) {
        Coordinate pos = person.getPos();

        if (isOverlapAboveLeft(pos)){
            aboveLeft.interactionWithPeople(person);
        }
        if (isOverlapAbove(pos)){
            above.interactionWithPeople(person);
        }
        if(isOverlapBelow(pos)){
            below.interactionWithPeople(person);
        }
        if(isOverlapBelowLeft(pos)){
            belowLeft.interactionWithPeople(person);
        }
    }
}
