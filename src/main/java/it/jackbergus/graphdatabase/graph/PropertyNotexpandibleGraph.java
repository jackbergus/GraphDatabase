/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.jackbergus.graphdatabase.graph;

import it.jackbergus.utils.datatypes.Pair;
import java.util.HashSet;
import java.util.Set;
import it.jackbergus.graphdatabase.Entity;
import it.jackbergus.graphdatabase.matrices.IMatrix;

/**
 * This class provides over a view of given relations. See IMatrix and IGraph
 * for comments
 *
 * @author gyankos
 */
public class PropertyNotexpandibleGraph implements IAdjacencyGraph {

    private String relationName;
    private double[][] core;
    private int M;
    
    public PropertyNotexpandibleGraph(String rel, int max) {
        this.relationName = rel;
        this.core = new double[max+1][max+1];
        M = max+1;
    }
    
    @Override
    public boolean has(long i, long j) {
        return (core[(int)i][(int)j]==0);
    }
    
    @Override
    public String getName() {
        return relationName;
    }
    @Override
    public void addEdge(Entity left, Entity right, double value) {
        core[(int)left.getIndex()][(int)right.getIndex()] = value;
    }

    @Override
    public void addEdge(long left, long right, double value) {
        core[(int)left][(int)right] = value;
    }
    
    @Override
    public void addEdge(long left, long right) {
        core[(int)left][(int)right] = 1;
    }

    
    @Override
    public void addEdge(Entity left, Entity right) {
        addEdge(left,right,1);
    }

    @Override
    public void removeEdge(Entity left, Entity right) {
        core[(int)left.getIndex()][(int)right.getIndex()] = 0;
    }

    @Override
    public double getEdge(Entity left, Entity right) {
        return core[(int)left.getIndex()][(int)right.getIndex()];
    }

    @Override
    public void clear() {
        for (int i=0; i<M; i++)
            for (int j=0; j<M; j++)
                core[i][j] = 0;
    }

    @Override
    public long nCols() {
        return M;
    }

    @Override
    public long nRows() {
        return M;
    }

    @Override
    public double get(long i, long j) {
        return core[(int)i][(int)j];
    }

    @Override
    public double get(Pair<Long,Long> x) {
        return core[x.getFirst().intValue()][x.getSecond().intValue()];
    }

    @Override
    public void incr(Pair<Long,Long> x, double val) {
        core[x.getFirst().intValue()][x.getSecond().intValue()]+=val;
    }

    @Override
    public void rem(long i, long j) {
        core[(int)i][(int)j] = 0;
    }

    @Override
    public void incr(long i, long j, double val) {
        core[(int)i][(int)j] += val;
    }

    @Override
    public void set(long i, long j, double val) {
        core[(int)i][(int)j] = val;
    }

    @Override
    public void set(Pair<Long,Long> x, double val) {
        core[x.getFirst().intValue()][x.getSecond().intValue()]=val;
    }

    @Override
    public Set<Pair<Long,Long>> getValueRange() {
        Set<Pair<Long,Long>> toret = new HashSet<>();
        for (int i=0; i<M; i++)
            for (int j=0; j<M; j++)
                toret.add(new Pair<>((long)i,(long)j));
        return toret;
    }

    @Override
    public void removeRow(long i) {
        for (int j=0; j<M; j++)
            core[(int)i][j] = 0;
    }

    @Override
    public void removeCol(long j) {
        for (int i=0; i<M; i++)
            core[i][(int)j] = 0;
    }

    @Override
    public void removeEnt(long elem) {
        removeCol(elem);
        removeRow(elem);
    }

    @Override
    public void sum(IMatrix right) {
        if (((int)right.getMaxKey())>M)
            throw new IndexOutOfBoundsException("The incoming right matrix is bigger than the immutable one");
        for (Pair<Long,Long> p : right.getValueRange()) {
            this.incr(p, right.get(p));
        }
    }

    @Override
    public void diff(IMatrix right) {
        if (((int)right.getMaxKey())>M)
            throw new IndexOutOfBoundsException("The incoming right matrix is bigger than the immutable one");
        for (Pair<Long,Long> p : right.getValueRange()) {
            this.incr(p, -right.get(p));
        }
    }

    @Override
    public Set<Long> getOut(long o) {
        Set<Long> toret = new HashSet<>();
        int i = (int)o;
        for (int j=0; j<M; j++) {
            if (core[i][j]!=0)
                toret.add((long)i);
        }
        return toret;
    }

    @Override
    public Set<Long> getIn(long o) {
        Set<Long> toret = new HashSet<>();
        int j = (int)o;
        for (int i=0; i<M; i++) {
            if (core[i][j]!=0)
                toret.add((long)i);
        }
        return toret;
    }

    public double[][] getCore() {
        return this.core;
    }

    @Override
    public long getMaxKey() {
        return M-1;
    }

    @Override
    public long getMinKey() {
        return 0;
    }

    @Override
    public void save() {
        //
    }

}
