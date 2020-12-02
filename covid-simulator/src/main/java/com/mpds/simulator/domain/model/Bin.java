package com.mpds.simulator.domain.model;

import com.mpds.simulator.application.service.SequenceManager;
import com.mpds.simulator.domain.model.events.DomainEvent;
import com.mpds.simulator.domain.model.events.InfectionReported;
import com.mpds.simulator.domain.model.events.PersonContact;
import com.mpds.simulator.domain.model.events.PersonHealed;
import com.mpds.simulator.port.adapter.kafka.DomainEventPublisher;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

@Data
@Slf4j
public class Bin {

    private Coordinate ulCorner;
    private Coordinate lrCorner;
    private Coordinate overlapCorner;
    private GridBins grid;
    private ArrayList<Person> peopleInBin;
    private ArrayList<Person> peopleInOverlap;
    private ArrayList<Person> toMove;
    private int infectionDistance;
    private int infectionTime;
    private int time;
    private int gridSizeRow;
    private int gridSizeCol;
    private DomainEventPublisher publisher;

    private ArrayList<Person[]> contacts;

    private BinarySearchTree2d searchTree;
    private BinarySearchLeaf firstLeaf;

    public Bin(Coordinate ulCorner, Coordinate lrCorner, Coordinate overlapSize, int infectionDistance, int infectionTime, GridBins grid, int searchTreeBinSize, DomainEventPublisher publisher){
        this.ulCorner = ulCorner;
        this.lrCorner = lrCorner;
        overlapCorner = this.lrCorner.addCoordinate(overlapSize);
        this.infectionDistance = infectionDistance;
        this.grid = grid;
        gridSizeRow = grid.getSize().getRow();
        gridSizeCol = grid.getSize().getCol();
        peopleInBin = new ArrayList<>();
        peopleInOverlap = new ArrayList<>();
        this.infectionTime = infectionTime;
        this.publisher = publisher;
        searchTree = new BinarySearchTree2d(true, ulCorner, lrCorner, searchTreeBinSize, null);
        firstLeaf = searchTree.connectLeaves().getLeft();
    }

    public void calcContactInfection(Person p1, Person p2){
        int distance = p1.getPos().distanceTo(p2.getPos());
        if(distance <= infectionDistance){
            System.out.println("contact:" + String.valueOf(p1.getId()) + " - " + String.valueOf(p2.getId()));
            //DomainEvent personContactEvent = new PersonContact(time, (long) p1.getId(), (long) p2.getId(), LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
            //this.grid.getDomainEventPublisher().sendMessages(personContactEvent).subscribe();
            if(p1.getInfected() > 0 && p2.getInfected() == 0){
                checkInfection(p1, p2, distance);
            } else if (p2.getInfected() > 0 && p1.getInfected() == 0){
                checkInfection(p2, p1, distance);
            }
        }
    }

    // Check if the infected person is within distance
    private void checkInfection(Person infectedPerson, Person healthyPerson, int distance) {
        if(healthyPerson.getRandomGen().nextInt(101) > distance + 1){
            healthyPerson.setInfected(infectionTime+1);
            //log.info("infection:" + infectedPerson.getId() + " - " + healthyPerson.getId());
            //DomainEvent domainEvent = new InfectionReported(time, (long) healthyPerson.getId(), LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
            //this.grid.getDomainEventPublisher().sendMessages(domainEvent).subscribe();
        }
    }

    public void contactsInfections(){
        Person p1, p2;
        int distance;
        for(int i=0; i<peopleInBin.size(); i++){
            p1 = peopleInBin.get(i);
            for(int j=i+1; j<peopleInBin.size(); j++){
                p2 = peopleInBin.get(j);
                calcContactInfection(p1, p2);
            }

            for (Person person : peopleInOverlap) {
                p2 = person;
                calcContactInfection(p1, p2);
            }
        }
        toMove = peopleInBin;
        peopleInBin = new ArrayList<>();
        peopleInOverlap = new ArrayList<>();
    }

    public void movePeople(){
        Person p;
        for (Person person : toMove) {
            p = person;
            p.move();
            if (p.getInfected() > 0) {
                p.decrementInfection();
                if (p.getInfected() == 0) {
                    //log.info("Person healed: " + p.getId());
//                    System.out.println("healed: " + p.getId());
                    //DomainEvent domainEvent = new PersonHealed(time, (long) p.getId(), LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC));
                    //this.grid.getDomainEventPublisher().sendMessages(domainEvent).subscribe();
                }
            }
            grid.insertPerson(p);
        }
        toMove = null;
    }

    public void calcInteractionsInList(Person currentPerson, LinkedListNode<Person> iterNode){
        System.out.println("calcInteractionsInList");
        while (iterNode != null){
            System.out.println(String.valueOf(iterNode.getContent().getId()));
            calcContactInfection(currentPerson, iterNode.getContent());
            iterNode = iterNode.getNext();
        }
    }


    public void recursiveRightTreeInteractions(BinarySearchTree2d tree, Person currentPerson){
        System.out.println("recursiveRightTreeInteractions");
        System.out.println(tree.getUpperLeft());
        System.out.println(tree.getLowerRight());
        System.out.println();

        if(tree.getLeftTree() != null){
            if(currentPerson.getPos().infectionRangeOverlaps(infectionDistance, tree.getUpperLeft(), tree.getLeftLowerRight())){
                recursiveRightTreeInteractions(tree.getLeftTree(), currentPerson);
            }
            if(currentPerson.getPos().infectionRangeOverlaps(infectionDistance, tree.getRightUpperLeft(), tree.getLowerRight())){
                recursiveRightTreeInteractions(tree.getRightTree(), currentPerson);
            }
        } else {
            if(currentPerson.getPos().infectionRangeOverlaps(infectionDistance, tree.getUpperLeft(), tree.getLeftLowerRight())){
                calcInteractionsInList(currentPerson, tree.getLeftLeaf().getPeople().getStart());
            }
            if(currentPerson.getPos().infectionRangeOverlaps(infectionDistance, tree.getRightUpperLeft(), tree.getLowerRight())){
                calcInteractionsInList(currentPerson, tree.getRightLeaf().getPeople().getStart());
            }
        }
    }

    public int isBorderCase(Coordinate upperLeft, Coordinate lowerRight){

        if (upperLeft.getRow() != 0){
            if(upperLeft.getCol() != 0){
                if(lowerRight.getRow() != gridSizeRow){

                    if (lowerRight.getCol() != gridSizeCol) {
                        return 0; // normal
                    } else{
                        return 1; // right
                    }

                } else {
                    if (lowerRight.getCol() != gridSizeCol){
                        return 2; // bottom
                    } else {
                        return 3; // bottom right
                    }
                }

            } else {
                if (lowerRight.getRow() != gridSizeRow){
                    return 4; // left
                } else {
                    return 5; // bottom left
                }
            }
        } else {
            if(upperLeft.getCol() != 0){
                if(lowerRight.getCol() != gridSizeCol){
                    return 6; // top
                } else {
                    return 7; // top right
                }
            } else {
                return 8; // top left
            }
        }
    }

    public void calcInteractions(BinarySearchLeaf currentLeaf, LinkedListNode<Person> currentNode){

        System.out.println("calcInteractions");
        System.out.println(currentLeaf.getUpperLeft());
        System.out.println(currentNode.getContent().getId());
        System.out.println();

        Person currentPerson = currentNode.getContent();
        calcInteractionsInList(currentPerson, currentNode.getNext());

        Coordinate position = currentPerson.getPos();

        if (position.infectionRangeContained(infectionDistance, currentLeaf.getUpperLeft(), currentLeaf.getLowerRight(), isBorderCase(currentLeaf.getUpperLeft(), currentLeaf.getLowerRight()))){System.out.println("all in leaf"); return; }

        BinarySearchTree2d parent = currentLeaf.getParent();

        System.out.println("parent");
        System.out.println(parent.getUpperLeft());
        System.out.println(parent.getLowerRight());
        System.out.println();

        if(currentLeaf == parent.getLeftLeaf()) {
            calcInteractionsInList(currentPerson, parent.getRightLeaf().getPeople().getStart());
        }

        BinarySearchTree2d child = parent;
        parent = child.getParent();

        System.out.println("parent");
        System.out.println(parent.getUpperLeft());
        System.out.println(parent.getLowerRight());
        System.out.println();
        while (!position.infectionRangeContained(infectionDistance, child.getUpperLeft(), child.getLowerRight(), isBorderCase(child.getUpperLeft(), child.getLowerRight())) && parent != null){

            System.out.println("parent loop");
            System.out.println(parent.getUpperLeft());
            System.out.println(parent.getLowerRight());
            System.out.println();

            if (child == parent.getLeftTree()){
                    if (position.infectionRangeOverlaps(infectionDistance, parent.getRightUpperLeft(), parent.getLowerRight())){
                        recursiveRightTreeInteractions(parent.getRightTree(),currentPerson);
                    }
            }

            child = parent;
            parent = child.getParent();
        }

    }


    public void iteration(){
        BinarySearchLeaf currentLeaf = firstLeaf;
        //<Person> currentList;
        LinkedListNode<Person> currentNode;
        while (currentLeaf != null){
            currentNode = firstLeaf.getPeople().getStart();
            while (currentNode != null){
                calcInteractions(currentLeaf, currentNode);
                return;
                //currentNode = currentNode.getNext();
            }
            //currentLeaf = currentLeaf.getNext();
        }
    }
}
