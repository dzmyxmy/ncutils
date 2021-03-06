/*******************************************************************************
 * Copyright (c) 2012, EPFL - ARNI
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *      * Redistributions of source code must retain the above copyright
 *        notice, this list of conditions and the following disclaimer.
 *      * Redistributions in binary form must reproduce the above copyright
 *        notice, this list of conditions and the following disclaimer in the
 *        documentation and/or other materials provided with the distribution.
 *      * Neither the name of the EPFL nor the
 *        names of its contributors may be used to endorse or promote products
 *        derived from this software without specific prior written permission.
 *  
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
 *  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

import ch.epfl.arni.ncutils.CodingVectorDecoder;
import ch.epfl.arni.ncutils.FiniteField;
import ch.epfl.arni.ncutils.Vector;
import java.util.Map;
import java.util.Random;

/**
 *
 * An example on how to use the ncutils library to obtain
 * the inverse matrix of a square matrix. To do so first
 * a random square matrix is created, then using the
 * CodingVectorDecoder the matrix is inverted. Finally the
 * code checks that the obtained matrix is indeed the inverse.
 *
 * @author lokeller
 */
public class CodingVectorLevelExample {

    public static void main(String[] args) {

        FiniteField ff = FiniteField.getDefaultFiniteField();

        /* initializes a random generator with a fixed seed to obtain
         * deterministic results.
         */
        Random r = new Random(12312);

        /**
         * This is the number of different blocks that are being sent
         */
        int size = 500;

        /*
         * Creates as many linearly independent vectors as possible
         * by drawing random coefficients (this will create errors in
         * general with a small probability, with this random seed it
         * will not)
         *
         */
        Vector[] vectors = new Vector[size];

        for (int i = 0; i < size; i++) {
            vectors[i] = new Vector(size, ff);

            for (int j = 0; j < size ; j++) {
                vectors[i].setCoordinate(j, r.nextInt(FiniteField.getDefaultFiniteField().getCardinality()));
            }                        
            

        }

        /* initialize a decoder */
        CodingVectorDecoder d = new CodingVectorDecoder(size, ff);
        //Decoder d = new VectorDecoder();

        /* store the start time of the decoding */
        long m = System.currentTimeMillis();

        Vector[] inverse = new Vector[size];

        /* decode one after another all the coding vectors */
        for (int i = 0 ; i < size ; i++) {

            /* the decode method returns the blocks that can be decoded
             * from the vector given as a parameter and the vectors that
             * were previously inserted
             */

            Map<Integer,Vector> o = d.addVector(vectors[i]);

            if ( o != null) {
	            for (Map.Entry<Integer, Vector> entry : o.entrySet()) {
	                inverse[entry.getKey()] = entry.getValue();
	            }
            }

        }

        /*
        for (int i= 0 ; i < size ; i++) {
            for (int j = 0 ; j < size ; j++) {
                System.out.print(" " + inverse[i].getCoordinate(j));
            }

            System.out.println();
        }

        System.out.println("-----------------");
*/
        for (int i= 0 ; i < size ; i++) {
            for (int j = 0 ; j < size ; j++) {

                int sum = 0;
                for (int k = 0 ; k < size ; k++) {
                    sum = ff.sum[sum][ff.mul[vectors[i].getCoordinate(k)][inverse[k].getCoordinate(j)]];
                }

  //              System.out.print(" " + sum);

                if (i == j && sum != 1) throw new RuntimeException();
                if (i != j && sum != 0) throw new RuntimeException();
            }
    //         System.out.println();
        }

        System.out.println("Total decoding time :" + (System.currentTimeMillis() - m));
    }
    
}
