/*******************************************************************************
 * Copyright 2016 Antoine Nicolas SAMAHA
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.foc.util;

public class CRC16CCITT_2 extends CRC16CCITT_1{

	public CRC16CCITT_2(String sText) {
		super(sText);
	}

	protected void augmentMessageForGoodCRC(){
	}

	protected void updateCrc(char ch) {
		/*
		 * based on code found at
		 * http://www.programmingparadise.com/utility/crc.html
		 */

		char i, xor_flag;

		/*
		 * Why are they shifting this byte left by 8 bits?? How do the low bits of
		 * the poly ever see it?
		 */
		ch <<= 8;

		for (i = 0; i < 8; i++) {
			if (((crc ^ ch) & 0x8000) != 0) {
				xor_flag = 1;
			} else {
				xor_flag = 0;
			}
			crc = (char) (crc << 1);
			if (xor_flag != 0) {
				crc = (char) (crc ^ poly);
			}
			ch = (char) (ch << 1);
		}
	}
}
