package com.mpds.simulator;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
class CovidSimulatorApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void testGrid() {
        Coordinate size = new Coordinate(22, 22);
        Coordinate binSize = new Coordinate(3, 3);
        Coordinate overlap = new Coordinate(1, 1);
        GridBins grid = new GridBins(size, binSize, overlap, 3, 14);
        Assert.isTrue(grid.bins[0][0].ulCorner.row == 0);
        Assert.isTrue(grid.bins[0][0].ulCorner.col == 0);
        Assert.isTrue(grid.bins[0][0].lrCorner.row == 2);
        Assert.isTrue(grid.bins[0][0].lrCorner.col == 2);
        Assert.isTrue(grid.bins[0][0].overlapCorner.row == 3);
        Assert.isTrue(grid.bins[0][0].overlapCorner.col == 3);

        Assert.isTrue(grid.bins[0][6].ulCorner.row == 0, String.valueOf(grid.bins[0][6].ulCorner.row));
        Assert.isTrue(grid.bins[0][6].ulCorner.col == 18, String.valueOf(grid.bins[0][6].ulCorner.col));
        Assert.isTrue(grid.bins[0][6].lrCorner.row == 2, String.valueOf(grid.bins[0][6].lrCorner.row));
        Assert.isTrue(grid.bins[0][6].lrCorner.col == 21, String.valueOf(grid.bins[0][6].lrCorner.col));
        Assert.isTrue(grid.bins[0][6].overlapCorner.row == 3, String.valueOf(grid.bins[0][6].overlapCorner.row));
        Assert.isTrue(grid.bins[0][6].overlapCorner.col == 21, String.valueOf(grid.bins[0][6].overlapCorner.col));

        Assert.isTrue(grid.bins[6][0].ulCorner.row == 18, String.valueOf(grid.bins[6][0].ulCorner.row));
        Assert.isTrue(grid.bins[6][0].ulCorner.col == 0, String.valueOf(grid.bins[6][0].ulCorner.col));
        Assert.isTrue(grid.bins[6][0].lrCorner.row == 21, String.valueOf(grid.bins[6][0].lrCorner.row));
        Assert.isTrue(grid.bins[6][0].lrCorner.col == 2, String.valueOf(grid.bins[6][0].lrCorner.col));
        Assert.isTrue(grid.bins[6][0].overlapCorner.row == 21, String.valueOf(grid.bins[6][0].overlapCorner.row));
        Assert.isTrue(grid.bins[6][0].overlapCorner.col == 3, String.valueOf(grid.bins[6][0].overlapCorner.col));

        Assert.isTrue(grid.bins[6][6].ulCorner.row == 18, String.valueOf(grid.bins[6][6].ulCorner.row));
        Assert.isTrue(grid.bins[6][6].ulCorner.col == 18, String.valueOf(grid.bins[6][6].ulCorner.col));
        Assert.isTrue(grid.bins[6][6].lrCorner.row == 21, String.valueOf(grid.bins[6][6].lrCorner.row));
        Assert.isTrue(grid.bins[6][6].lrCorner.col == 21, String.valueOf(grid.bins[6][6].lrCorner.col));
        Assert.isTrue(grid.bins[6][6].overlapCorner.row == 21, String.valueOf(grid.bins[6][6].overlapCorner.row));
        Assert.isTrue(grid.bins[6][6].overlapCorner.col == 21, String.valueOf(grid.bins[6][6].overlapCorner.col));

        Person p1 = new Person(1, new Coordinate(0, 0), 0, size);
        grid.insertPerson(p1);
        Assert.isTrue(!grid.bins[0][0].peopleInBin.isEmpty());

        Person p2 = new Person(2, new Coordinate(18, 18), 0, size);
        grid.insertPerson(p2);
        Assert.isTrue(!grid.bins[6][6].peopleInBin.isEmpty());
        Assert.isTrue(!grid.bins[5][6].peopleInOverlap.isEmpty());
        Assert.isTrue(!grid.bins[6][5].peopleInOverlap.isEmpty());
        Assert.isTrue(!grid.bins[5][5].peopleInOverlap.isEmpty());

        Person p3 = new Person(3, new Coordinate(21, 21), 0, size);
        grid.insertPerson(p3);
        Assert.isTrue(!grid.bins[6][6].peopleInBin.isEmpty());
        Assert.isTrue(grid.bins[6][6].peopleInOverlap.isEmpty());

    }

    @Test
    public void testGrid2() {
        Coordinate size = new Coordinate(100, 47);
        Coordinate binSize = new Coordinate(10, 6);
        Coordinate overlap = new Coordinate(3, 2);
        GridBins grid = new GridBins(size, binSize, overlap, 3, 14);

        Assert.isTrue(grid.bins[0][7].ulCorner.row == 0, String.valueOf(grid.bins[0][7].ulCorner.row));
        Assert.isTrue(grid.bins[0][7].ulCorner.col == 42, String.valueOf(grid.bins[0][7].ulCorner.col));
        Assert.isTrue(grid.bins[0][7].lrCorner.row == 9, String.valueOf(grid.bins[0][7].lrCorner.row));
        Assert.isTrue(grid.bins[0][7].lrCorner.col == 46, String.valueOf(grid.bins[0][7].lrCorner.col));
        Assert.isTrue(grid.bins[0][7].overlapCorner.row == 12, String.valueOf(grid.bins[0][7].overlapCorner.row));
        Assert.isTrue(grid.bins[0][7].overlapCorner.col == 46, String.valueOf(grid.bins[0][7].overlapCorner.col));

        Assert.isTrue(grid.bins[9][0].ulCorner.row == 90, String.valueOf(grid.bins[9][0].ulCorner.row));
        Assert.isTrue(grid.bins[9][0].ulCorner.col == 0, String.valueOf(grid.bins[9][0].ulCorner.col));
        Assert.isTrue(grid.bins[9][0].lrCorner.row == 99, String.valueOf(grid.bins[9][0].lrCorner.row));
        Assert.isTrue(grid.bins[9][0].lrCorner.col == 5, String.valueOf(grid.bins[9][0].lrCorner.col));
        Assert.isTrue(grid.bins[9][0].overlapCorner.row == 99, String.valueOf(grid.bins[9][0].overlapCorner.row));
        Assert.isTrue(grid.bins[9][0].overlapCorner.col == 7, String.valueOf(grid.bins[9][0].overlapCorner.col));

        Assert.isTrue(grid.bins[9][7].ulCorner.row == 90, String.valueOf(grid.bins[6][6].ulCorner.row));
        Assert.isTrue(grid.bins[9][7].ulCorner.col == 42, String.valueOf(grid.bins[6][6].ulCorner.col));
        Assert.isTrue(grid.bins[9][7].lrCorner.row == 99, String.valueOf(grid.bins[6][6].lrCorner.row));
        Assert.isTrue(grid.bins[9][7].lrCorner.col == 46, String.valueOf(grid.bins[6][6].lrCorner.col));
        Assert.isTrue(grid.bins[9][7].overlapCorner.row == 99, String.valueOf(grid.bins[6][6].overlapCorner.row));
        Assert.isTrue(grid.bins[9][7].overlapCorner.col == 46, String.valueOf(grid.bins[6][6].overlapCorner.col));
    }

    @Test
    public void testNextMove(){
        Coordinate size = new Coordinate(5,5);
        Person p = new Person(1, null, 0, size);
        for(int i=0; i<100; i++){
            p.move();
            //System.out.println("["+String.valueOf(p.pos.row)+"]["+String.valueOf(p.pos.col)+ "]");
        }
    }

    @Test
    public void testIteration(){
        Coordinate size = new Coordinate(1000, 1000);
        Coordinate binSize = new Coordinate(20, 20);
        Coordinate overlap = new Coordinate(6, 6);
        GridBins grid = new GridBins(size, binSize, overlap, 6, 30);
        grid.insertPerson(new Person(0, null, 100, size));
        for(int i=1; i<12000; i++){
            grid.insertPerson(new Person(i, null, 0, size));
        }
        for(int i=0; i<500; i++){
            System.out.println(i);
            grid.iteration();
        }
    }
}