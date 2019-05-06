package edu.augustana.csc335.netsim.mapgenerators;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import edu.augustana.csc335.netsim.model.SimDevice;
import edu.augustana.csc335.netsim.model.SimNetwork;

public class NetworkGenerators {

	private static Random rng = new Random();

	public static void main(String[] args) throws IOException {
		generateRandomLattice(4,4,150,200,0.2).saveNetworkToFile(new File("game_maps/grid.json"));
		
		//TODO: Fix the methods below, so that these three method calls actually work
		generateStarTopology(10).saveNetworkToFile(new File("game_maps/star.json"));
		generateRingTopology(12).saveNetworkToFile(new File("game_maps/ring.json"));
		generateFirewalledSubnets().saveNetworkToFile(new File("game_maps/firewalled.json"));
	}

	public static String makeRandomPassword(Random rng) {
		String ALPHABET = SimDevice.BRUTE_PASSWORD_ALPHABET;  
		return "" + ALPHABET.charAt(rng.nextInt(ALPHABET.length()));
	}
	
	public static List<Integer> makeRandomVulnerabilities(Random rng) {
		List<Integer> vulnerabilities = new ArrayList<Integer>();
		for (int i = 1; i <= 10; i++) {
			if (rng.nextDouble() < 0.15) { 
				vulnerabilities.add(i);
			}
		}
		return vulnerabilities;
	}
	
	/**
	 * Generates a grid/lattice network, possibly with some holes in it.
	 * @param rows 
	 * @param cols
	 * @param rowSpacing
	 * @param colSpacing
	 * @param holeChance - probability of putting in an empty spot instead of a device
	 * @return
	 */
	public static SimNetwork generateRandomLattice(int rows, int cols, int rowSpacing, int colSpacing, double holeChance) {
		SimNetwork net = new SimNetwork();
		SimDevice[][] grid = new SimDevice[rows+2][cols+2];

		// fancy Java 8 (stream) way to make a list of all IP address strings from "10.0.0.1" up to "10.0.0.254"   
		List<String> ipAddrList = IntStream.range(1, 254).boxed().map(i -> "10.0.0."+i).collect(Collectors.toCollection(ArrayList::new));
		Collections.shuffle(ipAddrList);  // randomize the IPs so players can't easily guess them
		
		for (int r = 1; r <= rows; r++) {
			for (int c = 1; c <= cols; c++) {
				if (rng.nextDouble() >= holeChance) {
					int deviceIndex = net.getDevices().size();
					
					String adminPassword = makeRandomPassword(rng);
					List<Integer> vulnerabilities = makeRandomVulnerabilities(rng);
					int x = (c-1)*colSpacing + 20; // add 20 pixels to give it a little padding on the sides
					int y = (r-1)*rowSpacing + 20;
					grid[r][c] = new SimDevice(deviceIndex,ipAddrList.get(deviceIndex),adminPassword,vulnerabilities,x,y,net);
					net.getDevices().add(grid[r][c]);
				}
			}
		}
		
		for (int r = 1; r <= rows; r++) {
			for (int c = 1; c <= cols; c++) {
				if (grid[r][c] != null) {
					List<SimDevice> neighbors = Arrays.asList(grid[r-1][c],grid[r+1][c],grid[r][c-1],grid[r][c+1]); 
					for (SimDevice neighbor : neighbors) {
						if (neighbor != null) {
							// note that it's necessary to add the neighbor relationship both directions a->b and b->a
							grid[r][c].addNeighbor(neighbor);
						}
					}					
				}
			}
		}
		return net;		
	}
	
	private static SimNetwork generateRingTopology(int numNodes) {
		//TODO: Fix this to generate a STAR topology network with the given number of nodes
		//      (For correct placement around a circle, use evenly spaced angles and Math.sin & Math.cos).
		SimNetwork net = new SimNetwork();				
		net.getDevices().add(new SimDevice(0,"10.0.0.1",makeRandomPassword(rng), makeRandomVulnerabilities(rng),300,300,net));
		return net;
	}

	private static SimNetwork generateStarTopology(int numSpokes) {
		//TODO: Fix this to generate a STAR topology network with one hub in the center & the specified number of spokes.
		//      (For correct placement around a circle, use evenly spaced angles and Math.sin & Math.cos).
		SimNetwork net = new SimNetwork();				
		net.getDevices().add(new SimDevice(0,"10.0.0.1",makeRandomPassword(rng), makeRandomVulnerabilities(rng),300,300,net));
		return net;
	}

	private static SimNetwork generateFirewalledSubnets() {
		//TODO: Create a network layout that has three different subnets (10.0.1.x, 10.0.2.x, and 10.0.3.x), 
		//      each of which have 3 devices in them.  None of these 9 devices should have any firewall settings!
		
		//      However, in addition to these 9 devices, you should also place several devices that do act as firewalls
		//      that will prevent the 10.0.1.x subnet from communicating with the 10.0.3.x subnet, and vice versa. 
		//      However, the 10.0.2.x subnet should still be able to communicate with any device in the network.
		// 
		//		You must make use of certain devices' firewall settings and use CIDR format to compactly specify a range
		// 			of addresses that those devices will block.
		
		SimNetwork net = new SimNetwork();				
		net.getDevices().add(new SimDevice(0,"10.0.0.1",makeRandomPassword(rng), makeRandomVulnerabilities(rng),300,300,net));
		return net;
	}

	
}
