/*
 * Dhruwang Kishorbhai Akbari
 * 251186352
 */

import java.io.*;
import java.util.ArrayList;

public class Query {
	
	public static void main(String[] args) {
		/*
		 * this part of the code makes a dictionary with all the key and contents from reading a file
		 */
	
		String key;
		String content;
		BSTOrderedDictionary dict = new BSTOrderedDictionary();
		BSTNode root = dict.getRoot();
		try {
			BufferedReader file = new BufferedReader(new FileReader(args[0]));
			key = file.readLine();
			while(key != null) {
				content = file.readLine();
				if(content.endsWith(".wav") || content.endsWith(".mid")) {
					dict.put(root, key, content, 2);
				}else if(content.endsWith(".jpg")|| content.endsWith(".gif")) {
					dict.put(root, key, content, 3);
				}else if(content.endsWith("html")) {
					dict.put(root, key, content, 4);
				}else {
					dict.put(root, key, content, 1);
				}
				key = file.readLine();
			}
			file.close();
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		/*
		 * This part of the code runs a while loop until the user enters "end". Asks for a command to implement
		 * on the current tree made in the Part above. Uses the methods in the ordered dictionary class.
		 * 
		 */
		try {
		StringReader keyboard = new StringReader();
		String line = keyboard.read("Enter next command: ");
		while(!line.equals("end")) {
			
			if(line.startsWith("get")) {
				String[] myLine = line.split(" ");
				String k = myLine[1];
				ArrayList<MultimediaItem> list = dict.get(root, k);
				if(list == null) {
					System.out.println("Key " + k +" is not in the ordered dictionary");
					NodeData pre = dict.predecessor(root, k);
					NodeData suc = dict.successor(root, k);
					if(pre == null && suc == null) {
						System.out.println("Preceding word: ");
						System.out.println("Following word: ");
					}else if(pre == null && suc != null) {
						System.out.println("Preceding word: ");
						System.out.println("Following word: " + suc.getName());
					}else if(suc == null && pre != null) {
						System.out.println("Preceding word: " + pre.getName());
						System.out.println("Following word: ");
					}else {
						System.out.println("Preceding word: " + pre.getName());
						System.out.println("Following word: " + suc.getName());
					}
				}else {
					for(int i = 0; i < list.size(); i++) {
						if(list.get(i).getType() == 1) {
							System.out.println(list.get(i).getContent());
						}else if(list.get(i).getType() == 2) {
							SoundPlayer player = new SoundPlayer();
							try {
								player.play(list.get(i).getContent());
							} catch (MultimediaException e) {
								e.printStackTrace();
							}
						}else if(list.get(i).getType() == 3) {
							PictureViewer display = new PictureViewer();
							try {
								display.show(list.get(i).getContent());
							} catch (MultimediaException e) {
								System.out.println(e);;
							}
						}else if(list.get(i).getType() == 4) {
							ShowHTML page = new ShowHTML();
							page.show(list.get(i).getContent());
						}
					}
				}
			}else if(line.startsWith("remove")) {
				String k = line.substring(7);
				try {
					dict.remove(root, k);
				} catch (DictionaryException e) {
					System.out.println(e);;
				}
			}else if(line.startsWith("delete")) {
				String[] myLine = line.split(" ");
				String k = myLine[1];
				int t = Integer.parseInt(myLine[2]);
				try {
					dict.remove(root, k, t);
				} catch (DictionaryException e) {
					System.out.println(e);
				}
			}else if(line.startsWith("add")) {
				String[] myLine = line.split(" ");
				String k = myLine[1];
				String c = myLine[2];
				int t = Integer.parseInt(myLine[3]);
				dict.put(root, k, c, t);
			}else if(line.startsWith("next")) {
				String[] myLine = line.split(" ");
				String k = myLine[1];
				String d = myLine[2];
				int t = Integer.parseInt(d);
				NodeData suc = dict.successor(root, k);
				if(suc == null) {
					System.out.println("There are no keys larger than or equal to "+ k);
				}else {
					for(int i =0; i < t; i++) {
						try {
						System.out.println(suc.getName());
						
							suc = dict.successor(root, suc.getName());
						}catch(NullPointerException e) {
							continue;
						}
					}
				}
				
			}else if(line.startsWith("prev")) {
				String[] myLine = line.split(" ");
				String k = myLine[1];
				String d = myLine[2];
				int t = Integer.parseInt(d);
				NodeData pre = dict.predecessor(root, k);
				if(pre == null) {
					System.out.println("There are no keys smaller than or equal to " + k);
				}else {
					for(int i = 0; i < t; i++) {
						try {
						System.out.println(pre.getName());
						
							pre = dict.predecessor(root, pre.getName());
						}catch (NullPointerException e) {
							continue;
						}
					}
				}
				
			}else if(line.startsWith("first")) {
				NodeData first = dict.smallest(root);
				System.out.println(first.getName());
				if(root.getData() == null) {
					System.out.println("The ordered dictionary is empty");
				}
			}else if(line.startsWith("last")) {
				NodeData last = dict.largest(root);
				System.out.println(last.getName());
				if(root.getData() == null) {
					System.out.println("The ordered dictionary is empty");
				}
			}else if(line.startsWith("size")) {
				System.out.println("There are "+ dict.getNumInternalNodes()+" keys in the ordered dictionary");
			}else {
				System.out.println("Invalid command. Enter again.");
			}
			line = keyboard.read("Enter next command: ");
		}
		}catch (ArrayIndexOutOfBoundsException e) {
			System.out.println(e);
		}
		System.out.println("End of Program");
	}
}
