package mdeServices.utils;

public class DigraphEdge
{
   /**
      Initializes a new DigraphEdge connecting two given DigraphNodes.
      @param originNode the node at the beginning of the edge
      @param destinationNode the node at the end of the edge
   */
   public DigraphEdge(DigraphNode originNode, DigraphNode destinationNode)
   {
      fromNode = originNode;
      toNode = destinationNode;
   }

   /** Returns the DigraphNode at which the DigraphEdge begins
    */
   public DigraphNode getFromNode() { return fromNode; }

   /** Returns the DigraphNode at which the DigraphEdge ends
    */
   public DigraphNode getToNode()   { return toNode; }

   private DigraphNode fromNode;
   private DigraphNode toNode;

} // end class DigraphEdge

