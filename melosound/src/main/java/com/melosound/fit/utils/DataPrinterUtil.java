package com.melosound.fit.utils;

import java.nio.ByteBuffer;

public class DataPrinterUtil {
	 public static void printBinary(int number) {
		 System.out.println(Integer.toBinaryString(number));
	 }
	 
	 public static void printBinaryArray(int[] numbers) {
		 for(int number : numbers) {
			 printBinary(number);
         }
	 }
	 
	 public static void printHexadecimal(int number) {
		 System.out.println(Integer.toHexString(number));
	 }
		 
	 public static void printHexadecimalArray(int[] numbers) {
		 for(int number : numbers) {
			 printHexadecimal(number);
		 }
	 }
	 
	 public static void printBinaryByteBuffer(ByteBuffer byteBuffer) {
		 while(byteBuffer.hasRemaining()) {
	            byte b = byteBuffer.get();
	            System.out.println(Integer.toBinaryString(b & 0xFF));
		 }
	 }
	 
	 public static void printHexadecimalByteBuffer(ByteBuffer byteBuffer) {
		 while(byteBuffer.hasRemaining()) {
	            byte b = byteBuffer.get();
	            System.out.println(Integer.toHexString(b & 0xFF));
		 }
	 }
}
