package mdeServices.utils;

import java.util.*;
import java.lang.reflect.*;

import mdeServices.metamodel.Classifier;

/** A class for representing directed graphs */

public class Digraph {
	
	/*  Based on the digraph classe provided by 2001 James A. Mason and licensed under 
	Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0)*/

    private ArrayList nodes;
    private ArrayList edges;
    
	/**
	    Initializes a new Digraph with empty lists of nodes and
	    edges.
	 */
	   public Digraph()
	   {  digraphEdges = new ArrayList();
	      digraphNodes = new ArrayList();
	   }

	   /**
	      Adds a new edge of the appropriate DigraphEdge subclass
	      to connect two given nodes indicated by their indices
	      (0, 1, 2, ... ) in the list of DigraphNodes for the Digraph.
	      @return the edge added, or null if unsuccessful.
	   */
	   public DigraphEdge addEdgeFromTo(int nodeIndex1, int nodeIndex2)
	      throws ClassNotFoundException, InvocationTargetException,
	             InstantiationException, IllegalAccessException
	   {  int numberOfNodes = digraphNodes.size();
	      if (nodeIndex1 >= numberOfNodes ||
	         nodeIndex2 >= numberOfNodes ||
	         nodeIndex1 < 0 || nodeIndex2 < 0) return null;
	      DigraphNode fromNode =
	         (DigraphNode)digraphNodes.get(nodeIndex1);
	      DigraphNode toNode =
	         (DigraphNode)digraphNodes.get(nodeIndex2);
	      /* This method uses "reflection".  See Core Java 1.2, Vol. 1,
	         pp. 204ff, esp. p. 205, 207, 211.
	      */
	      Class edgeClass = Class.forName(EdgeClassName);
	      Constructor[] constructors = edgeClass.getConstructors();
	      Object[] args = { fromNode, toNode };
	      DigraphEdge newEdge =
	              (DigraphEdge) constructors[0].newInstance(args);
	      fromNode.addOutEdge(newEdge);
	      toNode.addInEdge(newEdge);
	      digraphEdges.add(newEdge);
	      return newEdge;
	   } // end addEdgeFromTo

	   
	   /**
	      Adds a new edge of the appropriate DigraphEdge subclass
	      to connect two given nodes, provided the two nodes are
	      nodes of the receiver Digraph.
	      @param node1 the node that the edge is to go from
	      @param node2 the node that the edge is to go to
	      @return the new edge; or null if unsuccessful
	   */
	   public DigraphEdge addEdgeFromNodeToNodeUsingUniqueLabels(
	      Classifier node1, Classifier node2)
	   {  int j; int k;  // index into digraphNodes vector
	      for (j=0; j < digraphNodes.size(); ++j)
	         if ( ((DigraphNode) digraphNodes.get(j)).getLabel() == node1) break;
	      if (j >= digraphNodes.size()) // node1 not found
	         return null;
	      for (k=0; k< digraphNodes.size(); ++k)
		         if ( ((DigraphNode) digraphNodes.get(k)).getLabel() == node2) break;
		   if (k >= digraphNodes.size()) // node2 not found
	         return null;
      
		   DigraphNode origin=  ((DigraphNode)   digraphNodes.get(j));
		   DigraphNode end=  ((DigraphNode)   digraphNodes.get(k));
		   DigraphEdge newEdge = new DigraphEdge( origin, end);
	       origin.addOutEdge(newEdge);
	      end.addInEdge(newEdge);
	      digraphEdges.add(newEdge);
	      return newEdge;
	   } // end addEdgeFromNodeToNode

	   /**
	      Adds a new edge of the appropriate DigraphEdge subclass
	      to connect two given nodes, provided the two nodes are
	      nodes of the receiver Digraph.
	      @param node1 the node that the edge is to go from
	      @param node2 the node that the edge is to go to
	      @return the new edge; or null if unsuccessful
	   */
	   public DigraphEdge addEdgeFromNodeToNode(
	      DigraphNode node1, DigraphNode node2)
	      throws ClassNotFoundException, InvocationTargetException,
	             InstantiationException, IllegalAccessException
	   {  int j;  // index into digraphNodes vector
	      for (j=0; j < digraphNodes.size(); ++j)
	         if (digraphNodes.get(j) == node1) break;
	      if (j >= digraphNodes.size()) // node1 not found
	         return null;
	      for (j=0; j < digraphNodes.size(); ++j)
	         if (digraphNodes.get(j) == node2) break;
	      if (j >= digraphNodes.size()) // node2 not found
	         return null;
	      // Both nodes have been found
	      /* This method uses "reflection".  See Core Java 1.2, Vol. 1,
	         pp. 204ff, esp. p. 205, 207, 211.
	       */
	      Class edgeClass = Class.forName(EdgeClassName);
	      Constructor[] constructors = edgeClass.getConstructors();
	      Object[] args = { node1, node2 };
	      DigraphEdge newEdge =
	              (DigraphEdge) constructors[0].newInstance(args);
	      node1.addOutEdge(newEdge);
	      node2.addInEdge(newEdge);
	      digraphEdges.add(newEdge);
	      return newEdge;
	   } // end addEdgeFromNodeToNode

	   /**
	      Adds a new node to the Digraph and returns it.
	   */
	   public DigraphNode addNode(Classifier label)
	   {  DigraphNode newNode = new DigraphNode(label);
	      digraphNodes.add(newNode);
	      return newNode;
	   }

	  
	   /**
	      Returns the DigraphEdge at given position in the
	      list of edges; null if no such edge.
	      @param pos the index of the edge to be found
	    */
	   public DigraphEdge edgeAt(int pos)
	   {  if (pos < 0 || pos >= digraphEdges.size())
	         return null;
	      return (DigraphEdge)digraphEdges.get(pos);
	   } // end edgeAt

	   /**
	      Returns the ArrayList of DigraphEdges in the Digraph
	    */
	   public ArrayList getEdges() { return digraphEdges; }

	   /**
	      Returns the ArrayList of DigraphNodes in the Digraph
	    */
	   public ArrayList getNodes() { return digraphNodes; }

	
	   /**
	      Returns the DigraphNode at given position in the
	      list of nodes; null if no such node.
	      @param pos the index of the node to be found
	    */
	   public DigraphNode nodeAt(int pos)
	   {  if (pos < 0 || pos >= digraphNodes.size())
	         return null;
	      return (DigraphNode)digraphNodes.get(pos);
	   } // end nodeAt

	   /**
	      Returns the number of edges in the Digraph
	    */
	   public int numberOfEdges() { return digraphEdges.size(); }

	   /**
	      Returns the number of nodes in the Digraph
	    */
	   public int numberOfNodes() { return digraphNodes.size(); }

	   /**
	      Removes a given edge, provided it is in the Digraph.
	      Also updates the starting and ending nodes of the edge
	      appropriately.
	      @param aDigraphEdge the edge to be removed
	      @return the edge removed, or null if the edge is
	      not in the Digraph
	    */
	   public DigraphEdge removeEdge(DigraphEdge aDigraphEdge)
	   {  for (int j=0; j < digraphEdges.size(); ++j)
	         if (digraphEdges.get(j) == aDigraphEdge)
	         {  digraphEdges.remove(j);
	            aDigraphEdge.getFromNode()
	               .removeOutEdge(aDigraphEdge);
	            aDigraphEdge.getToNode()
	               .removeInEdge(aDigraphEdge);
	            return aDigraphEdge;
	         }
	      return null; // edge not found
	   } // end removeEdge

	   /**
	      Removes a given node, provided it is in the Digraph,
	      after first removing all edges in and out of it.
	      @param aDigraphNode the node to be removed
	      @return the node removed, or null if the node is
	      not in the Digraph
	   */
	   public DigraphNode removeNode(DigraphNode aDigraphNode)
	   {  int j;  // index into digraphNodes vector
	      ArrayList edges; // a list of edges into or out of a node
	      int e;  // index into an ArrayList of DigraphEdges
	      for (j=0; j < digraphNodes.size(); ++j)
	         if (digraphNodes.get(j) == aDigraphNode) break;
	      if (j >= digraphNodes.size()) // node not found
	         return null;
	      // Remove all edges into the node from the Digraph:
	      edges = aDigraphNode.getInEdges();
	      e = edges.size();
	      while(e-- > 0)
	         removeEdge((DigraphEdge)edges.get(e));
	      // Remove all remaining edges out of the node from the
	      // Digraph:
	      edges = aDigraphNode.getOutEdges();
	      e = edges.size();
	      while(e-- > 0)
	         removeEdge((DigraphEdge)edges.get(e));
	      digraphNodes.remove(j);
	      return aDigraphNode;
	   } // end removeNode

	   /**
	      Returns an ArrayList containing the DigraphNodes in the Digraph
	      that have no outgoing edges.
	    */
	   public ArrayList sinks()
	   {  ArrayList result = new ArrayList(numberOfNodes());
	      DigraphNode node;
	      for(int j=0; j < numberOfNodes(); ++j) {
	         node = (DigraphNode)digraphNodes.get(j);
	         if ( node.outDegree()==0 )
	            result.add(node);
	         }
	      result.trimToSize();
	      return result;
	   } // end sinks

	   /**
	      Returns an ArrayList containing the DigraphNodes in the Digraph
	      that have no incoming edges.
	   */
	   public ArrayList<DigraphNode> sources()
	   {  ArrayList<DigraphNode> result = new ArrayList<DigraphNode>(numberOfNodes());
	      DigraphNode node;
	      for(int j=0; j < numberOfNodes(); ++j) {
	         node = (DigraphNode)digraphNodes.get(j);
	         if ( node.inDegree()==0 )
	            result.add(node);
	         }
	      result.trimToSize();
	      return result;
	   } // end sources

	    /**
	      Returns a string that represents the Digraph in the following
	      form:
	      - the number of nodes in digraphNodes;
	      - a parenthesized list of edge representations,
	        each a parenthesized pair: (fromNodeNumber, toNodeNumber)
	        where fromNodeNumber and toNodeNumber are indices of nodes
	        in digraphNodes.
	     */
	   public String toString()
	   {  int nodeNumber;
	      DigraphNode node;
	      DigraphEdge edge;
	      StringBuffer buff = new StringBuffer(10*numberOfEdges());
	      buff.append(""+numberOfNodes());
	      buff.append(" (");
	      Iterator it = digraphEdges.iterator();
	      while(it.hasNext()) {
	         edge = (DigraphEdge)it.next();
	         node = edge.getFromNode();
	         buff.append(" (");
	         for (nodeNumber = 0; nodeNumber < numberOfNodes();
	               ++nodeNumber)
	            if (node == digraphNodes.get(nodeNumber))
	               break;
	         buff.append(nodeNumber+" ");
	         node = edge.getToNode();
	         for (nodeNumber = 0; nodeNumber < numberOfNodes();
	               ++nodeNumber)
	            if (node == digraphNodes.get(nodeNumber))
	               break;
	         buff.append(nodeNumber+")");
	         }
	      buff.append(" )");
	      return buff.toString();
	   } // end toString

	   /**
	      Returns a copy of the receiver Digraph,
	      without sharing of nodes or edges.
	      Returns null if the receiver Digraph is ill-formed.
	   */
	/*   public Digraph copyDigraph()
	      throws ClassNotFoundException, InvocationTargetException,
	             InstantiationException, IllegalAccessException
	   {  // This method uses "reflection".
	      // See Core Java 1.2, Vol. 1, p. 205.
	      Digraph result = (Digraph)this.getClass().newInstance();
	      for (int n = digraphNodes.size(); n > 0; --n)
	         result.addNode();
	      DigraphEdge edge;
	      DigraphNode node1;
	      int node1Index;
	      DigraphNode node2;
	      int node2Index;
	      Iterator it = digraphEdges.iterator();
	      while(it.hasNext())
	         // For each edge in the receiver Digraph,
	         // find the indices of its starting node and
	         // ending node in the list of nodes for the
	         // receiver Digraph, and add a corresponding
	         // edge between the corresponding two nodes in
	         // the copy of the Digraph:
	      {  edge = (DigraphEdge)it.next();
	         node1 = edge.getFromNode();
	         node2 = edge.getToNode();
	         for (node1Index = 0;
	              node1Index < numberOfNodes(); ++node1Index)
	            if (digraphNodes.get(node1Index) == node1)
	               break;
	         if (node1Index >= numberOfNodes()) return null;
	            // ill-formed receiver Digraph
	         for (node2Index = 0;
	              node2Index < numberOfNodes(); ++node2Index)
	            if (digraphNodes.get(node2Index) == node2)
	               break;
	         if (node2Index >= numberOfNodes()) return null;
	            // ill-formed receiver Digraph
	         result.addEdgeFromTo(node1Index, node2Index);
	         }
	      return result;
	   } // end copyDigraph*/
	   
	   /**
	      Returns true of the Digraph has cycles; false if not
	    */
	 /*  public boolean isCyclic()
	      throws ClassNotFoundException, InvocationTargetException,
	             InstantiationException, IllegalAccessException
	   {  Digraph digraphCopy = copyDigraph();
	      ArrayList sources;
	      DigraphNode node;
	      while(digraphCopy.numberOfNodes() > 0) {
	         sources = digraphCopy.sources();
	         if (sources.size() == 0)
	            // There are no sources among the remaining nodes;
	            // so a cycle has been found.
	            return true;
	         Iterator sourceList = sources.iterator();
	         while(sourceList.hasNext()) {
	            node = (DigraphNode)sourceList.next();
	            digraphCopy.removeNode(node);
	            }
	         }
	      // If all nodes were ultimately removed as sources,
	      // then there are no cycles in the original Digraph:
	      return false;
	   } // end isCyclic*/


	   
	   protected ArrayList digraphEdges;
	   protected ArrayList digraphNodes;
	   static protected String EdgeClassName;
	      /* The purpose of the EdgeClassName variable is to allow the
	      instance methods addEdgeFromTo and addEdgeFromNodeToNode
	      to work correctly (by inheritance) with subclasses of the
	      Digraph class, which may use instances of subclasses of
	      the DigraphEdge class for their edges.  Each subclass of
	      Digraph which uses a different subclass of DigraphEdge must
	      simply assign the name of that subclass to EdgeClassName.
	      */
	} // end class Digraph

	
	
