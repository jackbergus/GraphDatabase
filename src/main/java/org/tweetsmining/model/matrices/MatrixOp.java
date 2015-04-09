/*
 * Copyright (C) 2015 vasistas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.tweetsmining.model.matrices;

/**
 *
 * @author vasistas
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import Jama.Matrix;
import disease.utils.datatypes.Pair;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.collections4.keyvalue.MultiKey;

/**
 *
 * @author Giacomo Bergami
 */
public abstract class MatrixOp {
    
        public static int getRow(Pair<Long,Long> x) {
        if (x==null)
            return -1;
        else
            return x.getFirst().intValue();
    }
    
    public static int getCol(Pair<Long,Long> x) {
        if (x==null)
            return -1;
        else
            return x.getSecond().intValue();
    }
       
    
    
   /**
    * Matrix sum
    * @param left
    * @param right
    * @return
    */
   public static IMatrix sum(IMatrix left, IMatrix right) {
       Set< Pair<Long, Long>> iter = left.getValueRange();
       iter.addAll(right.getValueRange());
       IMatrix g = new GuavaMatrix();
       iter.stream().forEach((x) -> {
           g.set(x,left.get(x)+right.get(x));
       });
       return g;
   }
   
   /**
    * MAtrix difference
    * @param left
    * @param right
    * @return
    */
   public static IMatrix diff(IMatrix left, IMatrix right) {
       Set< Pair<Long, Long>> iter = left.getValueRange();
       iter.addAll(right.getValueRange());
       IMatrix g = new GuavaMatrix();
       iter.stream().forEach((x) -> {
           g.set(x,left.get(x)-right.get(x));
       });
       return g;
   }
   
   /**
    * Matrix product
    * @param left
    * @param right
    * @return 
    */
   public static IMatrix prod(IMatrix left, IMatrix right) {
       IMatrix g = new GuavaMatrix();
       left.getValueRange().stream().forEach((l) -> {
           Long li = l.getSecond();
           right.getValueRange().stream().forEach((r) -> {
               Long ri = r.getFirst();
               if (li.equals(ri)) {
                   double lv = (Double)left.get(l);
                   double rv = (Double)right.get(r);
                   //System.out.println(lv+" "+rv+" "+lv*rv);
                   g.incr(l.getFirst(),r.getSecond(),lv*rv);
               }
           });
});
       return g;
   }
   
   
   /**
    * Matrix divide
    * @param left
    * @param r
    * @return
    */
   public static IMatrix div(IMatrix left, double r) {
       IMatrix g = new GuavaMatrix();
       for (Pair<Long, Long> l : left.getValueRange()) {
               Long li = l.getFirst();
               Long ri = l.getSecond();
               g.incr(li,ri,(left.get(l)/r));
       }
       return g;
   }
   
     /**
    * Matrix divide
    * @param left
    * @param right
    * @return
    */
   public static IMatrix div(IMatrix left, IMatrix right) {
       IMatrix g = new GuavaMatrix();
       left.getValueRange().stream().forEach((l) -> {
           right.getValueRange().stream().forEach((r) -> {
               Long li = l.getFirst();
               Long ri = l.getSecond();
               if (li.equals(ri)) {
                   double lv = (Double)left.get(l);
                   double rv = (Double)right.get(r);
                   //System.out.println(lv+" "+rv+" "+lv*rv);
                   g.incr(li,ri,lv/rv);
               }
           });
       });
       return g;
   }
   

   /**
    * Matrix transpose
    * @param m
    * @return
    */
   public static IMatrix transpose(IMatrix m)  {
       IMatrix g = new GuavaMatrix();
       m.getValueRange().stream().forEach((k) -> {
           Long li = k.getFirst();
           Long ri = k.getSecond();
           g.set(ri, li, m.get(k));
       });
       return g;
   }
   
   public static GuavaMatrix toGMatrix(Matrix m) {
        GuavaMatrix toret = new GuavaMatrix();
        for (int i=0; i<m.getRowDimension(); i++)
            for (int j=0; j<m.getColumnDimension(); j++)
                toret.set(i, j, m.get(i, j));
        return toret;
    }

    public static Matrix toMatrix(IMatrix m) {
        int size = (int)m.getMaxKey();
        Matrix toret = new Matrix(size,size);
        try {
            for (Pair<Long, Long> x: m.getValueRange()) {
                toret.set(MatrixOp.getRow(x), MatrixOp.getCol(x), m.get(x));
            }
            return toret;
        } catch (Throwable ex) {
            return toret;
        }
    }
    
    public static double[] toColumn(IMatrix m) {
        if (m.nCols()==1) {
            double toret [] = new double[(int)m.nRows()];
            for (Pair<Long, Long> x : m.getValueRange()) {
                toret[x.getFirst().intValue()] = m.get(x);
            }
            return toret;
        } else
            return new double[0];
    }
   
   public static IMatrix stationaryDistribution(IMatrix m) {
       Matrix tmp = toMatrix(m);
       int N = tmp.getColumnDimension();
       Matrix B = tmp.minus(Matrix.identity(N, N));
       for (int j = 0; j < N; j++)
           B.set(0, j, 1.0);
       Matrix b = new Matrix(N, 1);
       b.set(0, 0, 1.0);
       return toGMatrix(B.solve(b));
   }
   
   public static IMatrix diagonal(double... d) {
       IMatrix tmp = new GuavaMatrix();
       for (int i=0; i<d.length; i++)
           tmp.set(i,i, d[i]);
       return tmp;
   }
   
   public static IMatrix diagonal(double val, int size) {
       IMatrix tmp = new GuavaMatrix();
       for (int i=0; i<size; i++)
           tmp.set(i,i, val);
       return tmp;
   }
   
   public static double[] rowSums(IMatrix m) {
       long size = Math.max(m.nCols(), m.nRows());
       double toret[] = new double[(int)size];
       for (int i=0; i<size; i++)
           toret[i]=0;
       for (int i=0; i<size; i++)
           for (int j=0; j<size; j++)
               toret[i] += m.get(i,j);
       return toret;
   }
   
   public static IMatrix regularizedLaplacianMatrix(IMatrix m) {
       IMatrix tmp = MatrixOp.prod(m, MatrixOp.transpose(m));
       long size = tmp.nCols();
       IMatrix i = diagonal(1,size);
       IMatrix d = diagonal(rowSums(tmp));
       IMatrix laplacian = new GuavaMatrix();
       for (int ii = 0; ii<size; ii++)
           for (int ji = 0; ji<size; ji++)
               laplacian.set(ii, ji, (d.get(ii, ji)-tmp.get(ii, ji))/ Math.sqrt(d.get(ii, ii)*d.get(ji, ji)));
       return laplacian;
   }
    
}

