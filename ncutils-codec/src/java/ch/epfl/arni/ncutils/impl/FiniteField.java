/*
 * Copyright (c) 2011, EPFL - ARNI
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the EPFL nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package ch.epfl.arni.ncutils.impl;


/**
 * This class represent a finite field.
 * 
 */
public class FiniteField {

	private static FiniteField finiteField;

    /**
     * Returns the finite field F2^8
     *
     * @return the finite field for F2^8
     */
    public static FiniteField getF256() {
    	
    	if ( finiteField == null) {
    		finiteField = new FiniteField(2,8);
    	}
    	
        return finiteField;
    }
    
    /**
     * Associates each field element to its inverse
     */
    public int[] inverse;

    /**
     * Associate two field elements to their sum
     */
    public int[][] sum;

    /**
     * Associate two field elements to their division
     */
    public int[][] div;

    /**
     * Associate two field elements to their substraction
     */
    public int[][] sub;


    /**
     * Associate two field elements to their multiplication
     */
    public int[][] mul;


    private int Q;

    /**
     * Constructs a new extension field
     *
     * @param q the prime used to create the polynomial
     * @param m the power of the polynomial
     */
    public FiniteField(int q, int m) {
        
        if (q < 1 || m < 0) throw new RuntimeException("Invalid field size");

        if (q != 2 || m > 16) throw new UnsupportedOperationException("Finite field not supported");

        this.Q = (int) Math.pow(q,m);

        inverse = new int[Q];
        sum = new int[Q][Q];
        mul = new int[Q][Q];
        div = new int[Q][Q];
        sub = new int[Q][Q];

        int [] primitive_polynomial = { 3, 7, 11, 19, 37, 67, 137,
                                        285, 529,1033,2053,4179,
                                        8219,17475, 32771, 69643 };      

        int c = primitive_polynomial[m - 1] - ( 1 << m );

        for (int i = 0 ; i < Q ; i++) {
            for (int j = 0 ; j < Q ; j++) {

                sum[i][j] = i ^ j;
                sub[i][j] = i ^ j;

                int a = i;
                int b = j;
                int p = 0;
                
                /* paesant's algorithm*/
                for ( int k = 0 ; k < m ; k++) {
                    if ( (b & 0x1) == 1) {
                        p = p ^ a;
                    }

                    boolean r = (a & (0x1 << (m-1))) > 0;
                    a = (a << 1) % Q;
                    if (r) {
                        a = a ^ c;
                    }
                    b = b >> 1;
                }

                mul[i][j] = p;

            }
        }

        for (int i = 0 ; i < Q ; i++) {
            for (int j = 0 ; j < Q ; j++) {

                div[mul[i][j]][i] = j;
                div[mul[i][j]][j] = i;

            }
        }

        for (int i = 1 ; i < Q ; i++) {
            inverse[i] = div[1][i];
        }

    }

    /**
     * Constructs a new finite field
     *
     * @param q the prime used to define the field
     */

    public FiniteField(int q) {
        this.Q = q;

        if (q < 1) throw new RuntimeException("Invalid field size");

        inverse = new int[Q];
        sum = new int[Q][Q];
        mul = new int[Q][Q];
        div = new int[Q][Q];
        sub = new int[Q][Q];
        
        /* build inverse table */
        for (int b = 1 ; b < Q ; b++) {
                for (int i = 1 ; i < Q ; i++) {
                        if ((i *  b) % Q == 1) {
                                inverse[b] = i;
                                break;
                        }
                }
        }

        /* build tables */
        for (int b = 0 ; b < Q ; b++) {
                for (int i = 0 ; i < Q ; i++) {
                    sum[b][i] = (b+i) % Q;
                    sub[b][i] = (b-i+Q) % Q;                    
                    mul[b][i] = (b * i) % Q;
                    div[b][i] = (b * inverse[i]) % Q;
                }
        }

    }

    /**
     * Returns the number of elements in the field
     * 
     * @return
     */
	public int getCardinality() {
        return Q;
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj instanceof FiniteField) {
            return ((FiniteField) obj).getCardinality() == Q;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + this.Q;
        return hash;
    }



}
