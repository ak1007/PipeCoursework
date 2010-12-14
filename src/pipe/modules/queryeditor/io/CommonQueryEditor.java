package pipe.modules.queryeditor.io;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pipe.common.PTNode;
import pipe.common.QueryConstants;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArc;
import pipe.modules.queryeditor.gui.performancetrees.macros.ArgumentNode;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroEditor;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ArithCompNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ArithOpNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ConvolutionNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.DisconNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.DistributionNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.FiringRateNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.InIntervalNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.MomentNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.NegationNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.PassageTimeDensityNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.PercentileNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ProbInIntervalNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ProbInStatesNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.RangeNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.SequentialNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.StatesAtTimeNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.SteadyStateProbNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.SteadyStateStatesNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.SubsetNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.ActionsNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.BoolNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.NumNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.StateFunctionNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.StatesNode;

public class CommonQueryEditor {

	public static JToolBar[] createToolbar(JToolBar toolBar1, JToolBar toolBar2, Map actions, String type){
		toolBar1 = new JToolBar();
		toolBar1.setRollover(true);
		toolBar1.setFloatable(false);
		toolBar2 = new JToolBar();
		toolBar2.setRollover(true);
		toolBar2.setFloatable(false);

		ButtonGroup drawButtons = new ButtonGroup();
		drawButtons.add(addIntelligentButton(toolBar1, (Action) actions.get("Draw Select"),type));
		drawButtons.add(addIntelligentButton(toolBar1, (Action) actions.get("Draw Drag"),type));
		drawButtons.add(addIntelligentButton(	toolBar1,(Action) actions.get("Draw PassageTimeDensity"), type));
		drawButtons.add(addIntelligentButton(toolBar1, (Action) actions.get("Draw Convolution"), type));
		drawButtons.add(addIntelligentButton(toolBar1, (Action) actions.get("Draw Distribution"), type));
		drawButtons.add(addIntelligentButton(toolBar1, (Action) actions.get("Draw Percentile"), type));
		drawButtons.add(addIntelligentButton(toolBar1, (Action) actions.get("Draw SteadyStateProb"), type));
		drawButtons.add(addIntelligentButton(	toolBar1,
				(Action) actions.get("Draw SteadyStateStates"), type));
		drawButtons.add(addIntelligentButton(toolBar1, (Action) actions.get("Draw StatesAtTime"), type));
		drawButtons.add(addIntelligentButton(toolBar1, (Action) actions.get("Draw ProbInInterval"), type));
		drawButtons.add(addIntelligentButton(toolBar1, (Action) actions.get("Draw ProbInStates"), type));
		drawButtons.add(addIntelligentButton(toolBar1, (Action) actions.get("Draw Moment"), type));
		drawButtons.add(addIntelligentButton(toolBar1, (Action) actions.get("Draw FiringRate"), type));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) actions.get("Draw InInterval"), type));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) actions.get("Draw Subset"), type));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) actions.get("Draw Negation"), type));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) actions.get("Draw Discon"), type));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) actions.get("Draw ArithComp"), type));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) actions.get("Draw ArithOp"), type));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) actions.get("Draw Num"), type));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) actions.get("Draw Range"), type));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) actions.get("Draw Actions"), type));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) actions.get("Draw States"), type));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) actions.get("Draw StateFunction"), type));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) actions.get("Draw Bool"), type));
		if (type.equalsIgnoreCase("queryEditor")){
			drawButtons.add(addIntelligentButton(toolBar1, (Action) actions.get("Draw Sequential"), type));
			drawButtons.add(addIntelligentButton(toolBar2, (Action) actions.get("Draw Macro"), type));
		}
		else if (type.equalsIgnoreCase("macroEditor")){
			drawButtons.add(addIntelligentButton(toolBar2, (Action) actions.get("Draw Argument"), type));
		}
		
		JToolBar[] toolbars = new JToolBar[2];
		toolbars[0] = toolBar1;
		toolbars[1] = toolBar2;
		return toolbars;
	}
	
	/**
	 * Creates a button that can keep in synch with its associated action i.e.
	 * will be automatically pressed if the equivalent menu option is clicked
	 * The new button is added to the "toolBar" parameter
	 * 
	 * @param toolBar -
	 *            the JToolBar to add the button to
	 * @param action -
	 *            the action that the button should perform
	 * @return
	 */
	private static AbstractButton addIntelligentButton(final JToolBar toolbar, final Action action, final String type)
	{
		URL selectedIconURL = (URL) action.getValue("selectedIconURL");
		ImageIcon selectedIcon = new ImageIcon(selectedIconURL);

		final AbstractButton b = new JToggleButton(action);
		b.setText(null);
		b.setSelectedIcon(selectedIcon);
		String actionName = (String) action.getValue(Action.NAME);
		b.setActionCommand(actionName);

		ActionListener actionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String actionName = e.getActionCommand();
				String msg = "";
				if (actionName.equals("Select"))
				{
					msg = QueryManager.addColouring("Select individual objects, arc endpoints or groups of objects.");
				}
				else if (actionName.equals("Drag"))
				{
					msg = QueryManager.addColouring("Drag around the contents of the canvas by holding down the mouse "
													+ "button and moving the mouse.");
				}
				else if (actionName.equals("Sequential") && type.equals("queryEditor"))
				{
					msg = SequentialNode.getNodeInfo();
				}
				else if (actionName.equals("PassageTimeDensity"))
				{
					msg = PassageTimeDensityNode.getNodeInfo();
				}
				else if (actionName.equals("Distribution"))
				{
					msg = DistributionNode.getNodeInfo();
				}
				else if (actionName.equals("Convolution"))
				{
					msg = ConvolutionNode.getNodeInfo();
				}
				else if (actionName.equals("ProbInInterval"))
				{
					msg = ProbInIntervalNode.getNodeInfo();
				}
				else if (actionName.equals("ProbInStates"))
				{
					msg = ProbInStatesNode.getNodeInfo();
				}
				else if (actionName.equals("Moment"))
				{
					msg = MomentNode.getNodeInfo();
				}
				else if (actionName.equals("Percentile"))
				{
					msg = PercentileNode.getNodeInfo();
				}
				else if (actionName.equals("FiringRate"))
				{
					msg = FiringRateNode.getNodeInfo();
				}
				else if (actionName.equals("SteadyStateProb"))
				{
					msg = SteadyStateProbNode.getNodeInfo();
				}
				else if (actionName.equals("SteadyStateStates"))
				{
					msg = SteadyStateStatesNode.getNodeInfo();
				}
				else if (actionName.equals("StatesAtTime"))
				{
					msg = StatesAtTimeNode.getNodeInfo();
				}
				else if (actionName.equals("InInterval"))
				{
					msg = InIntervalNode.getNodeInfo();
				}
				else if (actionName.equals("Subset"))
				{
					msg = SubsetNode.getNodeInfo();
				}
				else if (actionName.equals("Discon"))
				{
					msg = DisconNode.getNodeInfo();
				}
				else if (actionName.equals("Negation"))
				{
					msg = NegationNode.getNodeInfo();
				}
				else if (actionName.equals("ArithComp"))
				{
					msg = ArithCompNode.getNodeInfo();
				}
				else if (actionName.equals("ArithOp"))
				{
					msg = ArithOpNode.getNodeInfo();
				}
				else if (actionName.equals("Range"))
				{
					msg = RangeNode.getNodeInfo();
				}
				else if (actionName.equals("States"))
				{
					msg = StatesNode.getNodeInfo();
				}
				else if (actionName.equals("Actions"))
				{
					msg = ActionsNode.getNodeInfo();
				}
				else if (actionName.equals("Num"))
				{
					msg = NumNode.getNodeInfo();
				}
				else if (actionName.equals("Bool"))
				{
					msg = BoolNode.getNodeInfo();
				}
				else if (actionName.equals("StateFunction"))
				{
					msg = StateFunctionNode.getNodeInfo();
				}
				else if (actionName.equals("Macro") && type.equals("queryEditor"))
				{
					msg = MacroNode.getNodeInfo();
				}
				else if (actionName.equals("Argument") && type.equals("macroEditor"))
				{
					msg = ArgumentNode.getNodeInfo();
				}
				
				if (type.equals("queryEditor")){
					QueryManager.writeToInfoBox(msg);
				}
				else {
					MacroEditor.writeToInfoBox(msg);
				}
			}
		};
		b.addActionListener(actionListener);
		toolbar.add(b);

		action.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent pce)
			{
				if (pce.getPropertyName().equals("selected"))
				{
					b.setSelected(((Boolean) pce.getNewValue()).booleanValue());
				}
			}
		});
		return b;
	}
	
	/**
	 * Create a PerformanceTreeArc object from an PerformanceTreeArc DOM Element
	 * 
	 * @param inputElement -
	 *            Input PerformanceTreeArc DOM Element
	 * @return PerformanceTreeArc Object
	 */
	public static void createArc(final Element inputElement, final String type)
	{
		String arcID = null;
		String arcLabel = null;
		String arcSourceID = null;
		String arcTargetID = null;
		double arcStartX = 0;
		double arcStartY = 0;
		double arcEndX = 0;
		double arcEndY = 0;
		boolean arcRequired = true;
		boolean labelRequired = true;

		// retrieve info from element's attributes
		String retrievedArcID = inputElement.getAttribute("id");
		if (retrievedArcID.length() > 0)
			arcID = retrievedArcID;
		String retrievedArcLabel = inputElement.getAttribute("label");
		if (retrievedArcLabel.length() > 0)
			arcLabel = retrievedArcLabel;
		String retrievedArcSourceID = inputElement.getAttribute("source");
		if (retrievedArcSourceID.length() > 0)
			arcSourceID = retrievedArcSourceID;
		String retrievedArcTargetID = inputElement.getAttribute("target");
		if (retrievedArcTargetID.length() > 0)
			arcTargetID = retrievedArcTargetID;
		String retrievedArcRequired = inputElement.getAttribute("required");
		if (retrievedArcRequired.length() > 0)
		{
			if (retrievedArcRequired.equals("true"))
				arcRequired = true;
			else if (retrievedArcRequired.equals("false"))
				arcRequired = false;
		}
		String retrievedArcStartX = inputElement.getAttribute("startX");
		if (retrievedArcStartX.length() > 0)
			arcStartX = Double.parseDouble(retrievedArcStartX);
		String retrievedArcStartY = inputElement.getAttribute("startY");
		if (retrievedArcStartY.length() > 0)
			arcStartY = Double.parseDouble(retrievedArcStartY);
		String retrievedArcEndX = inputElement.getAttribute("endX");
		if (retrievedArcEndX.length() > 0)
			arcEndX = Double.parseDouble(retrievedArcEndX);
		String retrievedArcEndY = inputElement.getAttribute("endY");
		if (retrievedArcEndY.length() > 0)
			arcEndY = Double.parseDouble(retrievedArcEndY);

		if (type.equals("macroLoader")){
			PTNode parentNodeType = MacroLoader.macro.getMacroNode(arcSourceID).getNodeType();
			if (parentNodeType.equals(PTNode.MACRO))
				labelRequired = false;
		}
		else if (type.equals("queryLoader")){
			PTNode parentNodeType = QueryLoader.queryData.getNode(arcSourceID).getNodeType();
			if (parentNodeType.equals(PTNode.RESULT))
				labelRequired = false;
		}
		
		// create arc
		PerformanceTreeArc tempArc = new PerformanceTreeArc(arcStartX,
															arcStartY,
															arcEndX,
															arcEndY,
															arcSourceID,
															arcTargetID,
															arcLabel,
															labelRequired,
															arcRequired,
															arcID);

		// extract arcPathPoint details and attach them to arc
		float arcPathPointX = 0;
		float arcPathPointY = 0;
		boolean arcPathPointType = false;
		NodeList arcChildNodes = inputElement.getChildNodes();
		if (arcChildNodes.getLength() > 0)
		{
			// delete arc path points, so that we can add these ones
			tempArc.getArcPath().purgePathPoints();
			for (int i = 1; i < arcChildNodes.getLength() - 1; i++)
			{
				Node arcChildNode = arcChildNodes.item(i);
				if (arcChildNode instanceof Element)
				{
					Element arcElement = (Element) arcChildNode;
					if (arcElement.getNodeName().equals("arcpathpoint"))
					{
						String retrievedArcPathPointType = arcElement.getAttribute("type");
						if (retrievedArcPathPointType.length() > 0)
						{
							if (retrievedArcPathPointType.equals("true"))
								arcPathPointType = true;
							else if (retrievedArcPathPointType.equals("false"))
								arcPathPointType = false;
						}
						String retrievedArcPathPointX = arcElement.getAttribute("x");
						if (retrievedArcPathPointX.length() > 0)
						{
							arcPathPointX = Float.parseFloat(retrievedArcPathPointX);
							arcPathPointX += QueryConstants.ARC_CONTROL_POINT_CONSTANT + 1;
						}
						String retrievedArcPathPointY = arcElement.getAttribute("y");
						if (retrievedArcPathPointY.length() > 0)
						{
							arcPathPointY = Float.parseFloat(retrievedArcPathPointY);
							arcPathPointY += QueryConstants.ARC_CONTROL_POINT_CONSTANT + 1;
						}
						// attach arc path point to arc
						tempArc.getArcPath().addPoint(arcPathPointX, arcPathPointY, arcPathPointType);
					}
				}
			}
		}

		if (type.equals("macroLoader")){
			tempArc = MacroLoader.macro.addMacroArc(tempArc);
			MacroManager.getView().addNewMacroObject(tempArc);
		}
		else if (type.equals("queryLoader")){
			QueryLoader.queryData.addArc(tempArc);
		}
	}
	

}
