//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.tools.crs.georeferencing.application;

import static java.lang.Math.max;
import static org.deegree.tools.crs.georeferencing.communication.GUIConstants.JTEXTFIELD_COORDINATE_JUMPER;
import static org.deegree.tools.crs.georeferencing.communication.GUIConstants.MENUITEM_TRANS_HELMERT;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.vecmath.Point2d;

import org.deegree.commons.utils.Pair;
import org.deegree.coverage.raster.io.RasterIOOptions;
import org.deegree.cs.CRS;
import org.deegree.geometry.Envelope;
import org.deegree.geometry.GeometryFactory;
import org.deegree.geometry.primitive.Ring;
import org.deegree.rendering.r3d.model.geometry.GeometryQualityModel;
import org.deegree.rendering.r3d.model.geometry.SimpleAccessGeometry;
import org.deegree.rendering.r3d.opengl.display.OpenGLEventHandler;
import org.deegree.rendering.r3d.opengl.rendering.model.geometry.WorldRenderableObject;
import org.deegree.tools.crs.georeferencing.application.transformation.AffineTransformation;
import org.deegree.tools.crs.georeferencing.application.transformation.Helmert4Transform;
import org.deegree.tools.crs.georeferencing.application.transformation.Polynomial;
import org.deegree.tools.crs.georeferencing.application.transformation.TransformationMethod;
import org.deegree.tools.crs.georeferencing.application.transformation.TransformationMethod.TransformationType;
import org.deegree.tools.crs.georeferencing.communication.FileChooser;
import org.deegree.tools.crs.georeferencing.communication.GRViewerGUI;
import org.deegree.tools.crs.georeferencing.communication.GUIConstants;
import org.deegree.tools.crs.georeferencing.communication.PointTableFrame;
import org.deegree.tools.crs.georeferencing.communication.checkboxlist.CheckboxListTransformation;
import org.deegree.tools.crs.georeferencing.communication.dialog.ButtonPanel;
import org.deegree.tools.crs.georeferencing.communication.dialog.coordinatejump.CoordinateJumperTextfieldDialog;
import org.deegree.tools.crs.georeferencing.communication.dialog.error.ErrorDialog;
import org.deegree.tools.crs.georeferencing.communication.dialog.menuitem.OpenWMS;
import org.deegree.tools.crs.georeferencing.communication.dialog.menuitem.WMSParameterChooser;
import org.deegree.tools.crs.georeferencing.communication.dialog.option.GeneralPanel;
import org.deegree.tools.crs.georeferencing.communication.dialog.option.GenericSettingsPanel;
import org.deegree.tools.crs.georeferencing.communication.dialog.option.NavigationPanel;
import org.deegree.tools.crs.georeferencing.communication.dialog.option.OptionDialog;
import org.deegree.tools.crs.georeferencing.communication.dialog.option.SettingsPanel;
import org.deegree.tools.crs.georeferencing.communication.dialog.option.ViewPanel;
import org.deegree.tools.crs.georeferencing.communication.dialog.option.GenericSettingsPanel.PanelType;
import org.deegree.tools.crs.georeferencing.communication.panel2D.AbstractPanel2D;
import org.deegree.tools.crs.georeferencing.communication.panel2D.BuildingFootprintPanel;
import org.deegree.tools.crs.georeferencing.communication.panel2D.Scene2DPanel;
import org.deegree.tools.crs.georeferencing.model.CheckBoxListModel;
import org.deegree.tools.crs.georeferencing.model.Footprint;
import org.deegree.tools.crs.georeferencing.model.RowColumn;
import org.deegree.tools.crs.georeferencing.model.Scene2D;
import org.deegree.tools.crs.georeferencing.model.Scene2DImplShape;
import org.deegree.tools.crs.georeferencing.model.Scene2DImplWMS;
import org.deegree.tools.crs.georeferencing.model.dialog.OptionDialogModel;
import org.deegree.tools.crs.georeferencing.model.exceptions.NumberException;
import org.deegree.tools.crs.georeferencing.model.mouse.FootprintMouseModel;
import org.deegree.tools.crs.georeferencing.model.mouse.GeoReferencedMouseModel;
import org.deegree.tools.crs.georeferencing.model.points.AbstractGRPoint;
import org.deegree.tools.crs.georeferencing.model.points.FootprintPoint;
import org.deegree.tools.crs.georeferencing.model.points.GeoReferencedPoint;
import org.deegree.tools.crs.georeferencing.model.points.Point4Values;
import org.deegree.tools.crs.georeferencing.model.points.AbstractGRPoint.PointType;
import org.deegree.tools.crs.georeferencing.model.textfield.AbstractCoordinateJumperModel;
import org.deegree.tools.crs.georeferencing.model.textfield.CoordinateJumperModel;
import org.deegree.tools.rendering.viewer.File3dImporter;

/**
 * The <Code>Controller</Code> is responsible to bind the view with the model.
 * 
 * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class Controller {

    private GRViewerGUI view;

    private Scene2D model;

    private Scene2DPanel panel;

    private Scene2DValues sceneValues;

    private BuildingFootprintPanel footPanel;

    private PointTableFrame tablePanel;

    private RasterIOOptions options;

    private ParameterStore store;

    private Footprint footPrint;

    private CoordinateJumperModel textFieldModel;

    private OpenGLEventHandler glHandler;

    private GeoReferencedMouseModel mouseGeoRef;

    private FootprintMouseModel mouseFootprint;

    private Point2d changePoint;

    private boolean isHorizontalRefGeoref, isHorizontalRefFoot, start, isControlDown, selectedGeoref, selectedFoot;

    private boolean isZoomInGeoref, isZoomInFoot, isZoomOutGeoref, isZoomOutFoot;

    private GeometryFactory geom;

    private CRS sourceCRS, targetCRS;

    private List<Pair<Point4Values, Point4Values>> mappedPoints;

    private TransformationType transformationType;

    private TransformationMethod transform;

    // private ParameterStore store;

    private NavigationPanel optionNavPanel;

    private OptionDialogModel dialogModel;

    public int order;

    private SettingsPanel optionSettPanel;

    private OptionDialog optionDialog;

    // private CoordinateJumperSpinnerDialog jumperDialog;
    private CoordinateJumperTextfieldDialog jumperDialog;

    private OpenWMS wmsStartDialog;

    private WMSParameterChooser wmsParameter;

    private GenericSettingsPanel optionSettingPanel;

    private JToggleButton buttonPanGeoref, buttonPanFoot, buttonZoomInGeoref, buttonZoominFoot, buttonZoomoutGeoref,
                            buttonZoomoutFoot, buttonCoord;

    private ButtonModel buttonModel;

    private CheckboxListTransformation checkBoxListTransform;

    private CheckBoxListModel modelTransformation;

    private CheckBoxListModel modelFormats;

    private CheckBoxListModel modelCRS;

    private RowColumn rc;

    public Controller( GRViewerGUI view ) {

        geom = new GeometryFactory();
        // options = new RasterOptions( store ).getOptions();
        sceneValues = new Scene2DValues( geom );
        this.view = view;
        // this.model = model;
        this.panel = view.getScenePanel2D();
        this.footPanel = view.getFootprintPanel();

        this.start = false;

        this.glHandler = view.getOpenGLEventListener();
        // this.store = store;
        this.textFieldModel = new CoordinateJumperModel();
        this.dialogModel = new OptionDialogModel();
        AbstractPanel2D.selectedPointSize = this.dialogModel.getSelectionPointSize().first;
        AbstractPanel2D.zoomValue = this.dialogModel.getResizeValue().first;

        this.mappedPoints = new ArrayList<Pair<Point4Values, Point4Values>>();

        // model.init( options, sceneValues );
        view.addListeners( new ButtonListener() );
        // view.addChangeListener( new ChangeActionListener() );
        view.addHoleWindowListener( new HoleWindowListener() );

        initToggleButtons();

        // init the Checkboxlist for Transformation
        modelTransformation = new CheckBoxListModel();
        checkBoxListTransform = new CheckboxListTransformation( modelTransformation );
        view.addToMenuTransformation( checkBoxListTransform );
        checkBoxListTransform.addCheckboxListener( new ButtonListener() );

        // init the transformation method
        this.tablePanel = new PointTableFrame();
        this.tablePanel.addTableModelListener( new TableChangedEventListener() );
        this.tablePanel.addActionButtonListener( new ButtonListener() );
        transform = null;
        if ( transformationType == null ) {
            order = 1;
            for ( JCheckBox box : modelTransformation.getList() ) {
                if ( ( box ).getText().startsWith( MENUITEM_TRANS_HELMERT ) ) {
                    transformationType = TransformationType.Helmert_4;
                    view.activateTransformationCheckbox( box );
                    break;
                }
            }

        }

        isHorizontalRefGeoref = true;
        isHorizontalRefFoot = true;

    }

    /**
     * Initializes the navigation buttons that are registered for each map.
     */
    private void initToggleButtons() {
        buttonPanGeoref = view.getNavigationPanelGeoref().getButtonPan();
        buttonPanFoot = view.getNaviPanelFoot().getButtonPan();
        buttonZoomInGeoref = view.getNavigationPanelGeoref().getButtonZoomIn();
        buttonZoominFoot = view.getNaviPanelFoot().getButtonZoomIn();
        buttonZoomoutGeoref = view.getNavigationPanelGeoref().getButtonZoomOut();
        buttonZoomoutFoot = view.getNaviPanelFoot().getButtonZoomOut();
        buttonCoord = view.getNavigationPanelGeoref().getButtonZoomCoord();
    }

    /**
     * Selects one navigation button and deselects the other so that the focus is just on this one button. The
     * georeferencing for the georeferenced map will be turned off in this case. <br>
     * If the button is selected already, that will be deselected and there is a horizontal referencing possible again.
     * 
     * @param t
     *            the toggleButton that should be selected/deselected, not <Code>null</Code>.
     */
    private void selectGeorefToggleButton( JToggleButton t ) {
        boolean checkSelected = false;
        buttonModel = t.getModel();
        selectedGeoref = buttonModel.isSelected();
        if ( selectedGeoref == false ) {
            isHorizontalRefGeoref = true;
        } else {
            checkSelected = true;
            buttonPanGeoref.setSelected( false );
            buttonZoomInGeoref.setSelected( false );
            buttonZoomoutGeoref.setSelected( false );
            buttonCoord.setSelected( false );
            isHorizontalRefGeoref = false;
        }
        if ( t == buttonPanGeoref ) {
            buttonPanGeoref.setSelected( checkSelected );
        } else if ( t == buttonZoomInGeoref ) {
            buttonZoomInGeoref.setSelected( checkSelected );
        } else if ( t == buttonZoomoutGeoref ) {
            buttonZoomoutGeoref.setSelected( checkSelected );
        } else if ( t == buttonCoord ) {
            buttonCoord.setSelected( checkSelected );
            if ( checkSelected == true ) {
                // jumperDialog = new CoordinateJumperSpinnerDialog( view );
                jumperDialog = new CoordinateJumperTextfieldDialog( view );
                jumperDialog.getCoordinateJumper().setToolTipText( textFieldModel.getTooltipText() );
                jumperDialog.addListeners( new ButtonListener() );
                jumperDialog.setVisible( true );
            }
        }
    }

    /**
     * Selects one navigation button and deselects the other so that the focus is just on this one button. The
     * georeferencing for the footprint view will be turned off in this case. <br>
     * If the button is selected already, that will be deselected and there is a horizontal referencing possible again.
     * 
     * @param t
     *            the toggleButton that should be selected/deselected, not <Code>null</Code>.
     */
    private void selectFootprintToggleButton( JToggleButton t ) {

        boolean checkSelected = false;
        buttonModel = t.getModel();
        selectedFoot = buttonModel.isSelected();
        if ( selectedFoot == false ) {
            isHorizontalRefFoot = true;
        } else {
            checkSelected = true;
            buttonPanFoot.setSelected( false );
            buttonZoominFoot.setSelected( false );
            buttonZoomoutFoot.setSelected( false );
            isHorizontalRefFoot = false;
        }
        if ( t == buttonPanFoot ) {
            buttonPanFoot.setSelected( checkSelected );

        } else if ( t == buttonZoominFoot ) {
            buttonZoominFoot.setSelected( checkSelected );

        } else if ( t == buttonZoomoutFoot ) {
            buttonZoomoutFoot.setSelected( checkSelected );

        }

    }

    /**
     * Initializes the footprint scene.
     */
    private void initFootprintScene( String filePath ) {
        this.footPrint = new Footprint( sceneValues, geom );
        footPanel.addScene2DMouseListener( new Scene2DMouseListener() );
        footPanel.addScene2DMouseMotionListener( new Scene2DMouseMotionListener() );
        footPanel.addScene2DMouseWheelListener( new Scene2DMouseWheelListener() );
        // footPanel.addScene2DActionKeyListener( new Scene2DActionKeyListener() );
        // footPanel.addScene2DFocusListener( new Scene2DFocusListener() );

        // sceneValues.setDimenstionFootpanel( footPanel.getBounds() );
        mouseFootprint = new FootprintMouseModel();

        // List<WorldRenderableObject> rese = File3dImporter.open( view, store.getFilename() );
        List<WorldRenderableObject> rese = File3dImporter.open( view, filePath );
        sourceCRS = null;
        for ( WorldRenderableObject res : rese ) {

            sourceCRS = res.getBbox().getCoordinateSystem();

            glHandler.addDataObjectToScene( res );
        }
        List<float[]> geometryThatIsTaken = new ArrayList<float[]>();
        for ( GeometryQualityModel g : File3dImporter.gm ) {

            ArrayList<SimpleAccessGeometry> h = g.getQualityModelParts();
            boolean isfirstOccurrence = false;
            float minimalZ = 0;

            for ( SimpleAccessGeometry b : h ) {

                float[] a = b.getHorizontalGeometries( b.getGeometry() );
                if ( a != null ) {
                    if ( isfirstOccurrence == false ) {
                        minimalZ = a[2];
                        geometryThatIsTaken.add( a );
                        isfirstOccurrence = true;
                    } else {
                        if ( minimalZ < a[2] ) {

                        } else {
                            geometryThatIsTaken.remove( geometryThatIsTaken.size() - 1 );
                            minimalZ = a[2];
                            geometryThatIsTaken.add( a );
                        }
                    }

                }
            }

        }

        footPrint.generateFootprints( geometryThatIsTaken );

        sceneValues.setDimensionFootpanel( new Rectangle( footPanel.getBounds().width, footPanel.getBounds().height ) );
        footPanel.updatePoints( sceneValues );

        footPanel.setPolygonList( footPrint.getWorldCoordinateRingList(), sceneValues );

        footPanel.repaint();

    }

    /**
     * Initializes the georeferenced scene.
     */
    private void initGeoReferencingScene( Scene2D scene2d ) {
        mouseGeoRef = new GeoReferencedMouseModel();
        scene2d.init( sceneValues );
        init();

        panel.addScene2DMouseListener( new Scene2DMouseListener() );
        panel.addScene2DMouseMotionListener( new Scene2DMouseMotionListener() );
        panel.addScene2DMouseWheelListener( new Scene2DMouseWheelListener() );
        // panel.addScene2DActionKeyListener( new Scene2DActionKeyListener() );
        // panel.addScene2DFocusListener( new Scene2DFocusListener() );
    }

    /**
     * 
     * Controls the ActionListener
     * 
     * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    class ButtonListener implements ActionListener {
        private boolean isRunIntoTrouble = false;

        @Override
        public void actionPerformed( ActionEvent e ) {
            Object source = e.getSource();
            if ( source instanceof JCheckBox ) {
                JCheckBox selectedCheckbox = (JCheckBox) source;
                if ( ( selectedCheckbox ).getText().startsWith( GeneralPanel.SNAPPING_TEXT ) ) {

                    boolean isSnappingOn = false;
                    if ( dialogModel.getSnappingOnOff().second == false ) {
                        isSnappingOn = true;

                    } else {
                        isSnappingOn = false;
                    }
                    dialogModel.setSnappingOnOff( isSnappingOn );
                }
                if ( ( selectedCheckbox ).getText().startsWith( GUIConstants.MENUITEM_TRANS_POLYNOM_FIRST ) ) {

                    transformationType = TransformationType.PolynomialFirstOrder;

                    view.activateTransformationCheckbox( selectedCheckbox );
                }
                if ( ( selectedCheckbox ).getText().startsWith( GUIConstants.MENUITEM_TRANS_HELMERT ) ) {

                    transformationType = TransformationType.Helmert_4;
                    view.activateTransformationCheckbox( selectedCheckbox );
                }
                if ( ( selectedCheckbox ).getText().startsWith( GUIConstants.MENUITEM_TRANS_AFFINE ) ) {

                    transformationType = TransformationType.Affine;
                    view.activateTransformationCheckbox( selectedCheckbox );
                }

            }
            if ( source instanceof JTextField ) {
                JTextField tF = (JTextField) source;
                if ( tF.getName().startsWith( JTEXTFIELD_COORDINATE_JUMPER ) ) {
                    System.out.println( "[Controller] put something in the textfield: " + tF.getText() );

                    fireTextfieldJumperDialog();

                }

            }

            if ( source instanceof JToggleButton ) {
                JToggleButton tb = (JToggleButton) source;

                if ( tb.getName().startsWith( GUIConstants.JBUTTON_PAN ) ) {

                    if ( tb == buttonPanGeoref ) {
                        selectGeorefToggleButton( tb );
                        isZoomInGeoref = false;
                        isZoomOutGeoref = false;
                    } else {
                        selectFootprintToggleButton( tb );
                        isZoomInFoot = false;
                        isZoomOutFoot = false;
                    }
                }
                if ( tb.getName().startsWith( GUIConstants.JBUTTON_ZOOM_COORD ) ) {

                    if ( tb == buttonCoord ) {
                        selectGeorefToggleButton( tb );
                    } else {
                        selectFootprintToggleButton( tb );
                    }
                }
                if ( tb.getName().startsWith( GUIConstants.JBUTTON_ZOOM_IN ) ) {

                    if ( tb == buttonZoomInGeoref ) {
                        selectGeorefToggleButton( tb );
                        isZoomInGeoref = true;
                        isZoomOutGeoref = false;
                    } else {
                        selectFootprintToggleButton( tb );
                        isZoomInFoot = true;
                        isZoomOutFoot = false;
                    }
                }
                if ( tb.getName().startsWith( GUIConstants.JBUTTON_ZOOM_OUT ) ) {

                    if ( tb == buttonZoomoutGeoref ) {
                        selectGeorefToggleButton( tb );
                        isZoomInGeoref = false;
                        isZoomOutGeoref = true;
                    } else {
                        selectFootprintToggleButton( tb );
                        isZoomInFoot = true;
                        isZoomOutFoot = false;
                    }
                }
            }

            if ( source instanceof JButton ) {

                if ( ( (JButton) source ).getText().startsWith( PointTableFrame.BUTTON_DELETE_SELECTED ) ) {
                    int[] tableRows = tablePanel.getTable().getSelectedRows();

                    for ( int tableRow : tableRows ) {
                        FootprintPoint pointFromTable = new FootprintPoint(
                                                                            new Double(
                                                                                        tablePanel.getModel().getValueAt(
                                                                                                                          tableRow,
                                                                                                                          2 ).toString() ).doubleValue(),
                                                                            new Double(
                                                                                        tablePanel.getModel().getValueAt(
                                                                                                                          tableRow,

                                                                                                                          3 ).toString() ).doubleValue() );
                        boolean contained = false;
                        for ( Pair<Point4Values, Point4Values> p : mappedPoints ) {
                            double x = p.first.getWorldCoords().getX();
                            double y = p.first.getWorldCoords().getY();
                            if ( pointFromTable.getX() == x && pointFromTable.getY() == y ) {
                                removeFromMappedPoints( p );
                                contained = true;
                                panel.removeFromSelectedPoints( p.second );
                                footPanel.removeFromSelectedPoints( p.first );
                                break;
                            }
                        }
                        if ( contained == false ) {
                            footPanel.setLastAbstractPoint( null, null, null );
                            panel.setLastAbstractPoint( null, null, null );
                        }

                        tablePanel.removeRow( tableRow );

                    }

                    panel.repaint();
                    footPanel.repaint();
                }

                if ( ( (JButton) source ).getText().startsWith( PointTableFrame.BUTTON_DELETE_ALL ) ) {
                    removeAllFromMappedPoints();

                }
                if ( ( (JButton) source ).getText().startsWith( GUIConstants.COMPUTE_BUTTON_TEXT ) ) {
                    // swap the tempPoints into the map now
                    if ( footPanel.getLastAbstractPoint() != null && panel.getLastAbstractPoint() != null ) {
                        setValues();
                    }

                    // view.getMenuTransformation().setSelected( false );
                    // view.getMenuTransformation().getPopupMenu().setVisible( false );
                    // System.out.println( sourceCRS + " " + targetCRS );

                    switch ( transformationType ) {

                    case PolynomialFirstOrder:
                        // transform = new PolynomialFirstOrder( mappedPoints, footPrint, sceneValues, sourceCRS,
                        // targetCRS, 1 );
                        // transform = new LeastSquarePolynomial( mappedPoints, footPrint, sceneValues, sourceCRS,
                        // targetCRS, order );
                        order = 1;
                        transform = new Polynomial( mappedPoints, footPrint, sceneValues, sourceCRS, targetCRS, order );
                        break;
                    case Helmert_4:
                        order = 1;
                        transform = new Helmert4Transform( mappedPoints, footPrint, sceneValues, sourceCRS, targetCRS,
                                                           order );

                        break;
                    case Affine:
                        order = 1;
                        transform = new AffineTransformation( mappedPoints, footPrint, sceneValues, sourceCRS,
                                                              targetCRS, order );

                        break;
                    }
                    List<Ring> polygonRing = transform.computeRingList();

                    panel.setPolygonList( polygonRing, sceneValues );

                    panel.repaint();

                    reset();
                }
                if ( ( (JButton) source ).getText().startsWith( ButtonPanel.BUTTON_TEXT_CANCEL ) ) {
                    if ( optionDialog != null && optionDialog.isVisible() == true ) {
                        dialogModel.transferOldToNew();
                        AbstractPanel2D.selectedPointSize = dialogModel.getSelectionPointSize().first;
                        panel.repaint();
                        footPanel.repaint();
                        optionDialog.setVisible( false );
                    } else if ( jumperDialog != null && jumperDialog.isVisible() == true ) {
                        jumperDialog.setVisible( false );

                        selectedGeoref = false;
                        buttonModel.setSelected( false );
                        isHorizontalRefGeoref = true;

                    } else if ( wmsStartDialog != null && wmsStartDialog.isVisible() == true ) {
                        wmsStartDialog.setVisible( false );

                    } else if ( wmsParameter != null && wmsParameter.isVisible() == true ) {
                        wmsParameter.setVisible( false );
                        wmsStartDialog.setVisible( true );
                    }

                }
                if ( ( (JButton) source ).getText().startsWith( ButtonPanel.BUTTON_TEXT_OK ) ) {
                    if ( optionDialog != null && optionDialog.isVisible() == true ) {

                        if ( optionSettingPanel != null ) {

                            if ( optionSettingPanel instanceof ViewPanel ) {
                                // if the custom radiobutton is selected and there is something inside the textField
                                if ( !( (ViewPanel) optionSettingPanel ).getTextFieldCustom().getText().equals( "" )
                                     && ( (ViewPanel) optionSettingPanel ).getRadioCustom().getSelectedObjects() != null ) {
                                    // here you have to check about the input for the custom textfield. Keylistener for
                                    // the textfield while typing in is problematic because you can workaround with
                                    // copy&paste...so this should be the way to go.

                                    String textInput = ( (ViewPanel) optionSettingPanel ).getTextFieldCustom().getText();
                                    if ( AbstractCoordinateJumperModel.validateInt( textInput ) ) {
                                        dialogModel.setTextFieldKeyString( textInput );
                                        dialogModel.setSelectionPointSize( Integer.parseInt( dialogModel.getTextFieldKeyString().second ) );
                                        isRunIntoTrouble = false;
                                    } else {
                                        new ErrorDialog( optionDialog, JDialog.ERROR,
                                                         "Insert numbers only into the textField!" );
                                        isRunIntoTrouble = true;
                                    }

                                }
                            }
                            if ( optionSettingPanel instanceof GeneralPanel ) {
                                String p = ( (GeneralPanel) optionSettingPanel ).getTextField(
                                                                                               ( (GeneralPanel) optionSettingPanel ).getZoomValue() ).getText();
                                String p1 = p.replace( ',', '.' );
                                dialogModel.setResizeValue( new Double( p1 ).doubleValue() );
                                isRunIntoTrouble = false;
                            }
                        }
                        if ( isRunIntoTrouble == false ) {
                            dialogModel.transferNewToOld();
                            AbstractPanel2D.selectedPointSize = dialogModel.getSelectionPointSize().first;
                            panel.repaint();
                            footPanel.repaint();
                            optionDialog.setVisible( false );
                        }
                    } else if ( jumperDialog != null && jumperDialog.isVisible() == true ) {

                        fireTextfieldJumperDialog();
                    } else if ( wmsStartDialog != null && wmsStartDialog.isVisible() == true ) {
                        String mapURLString = wmsStartDialog.getTextField().getText();
                        wmsStartDialog.setVisible( false );
                        try {
                            wmsParameter = new WMSParameterChooser( wmsStartDialog, mapURLString );
                        } catch ( MalformedURLException e1 ) {
                            new ErrorDialog( wmsStartDialog, JDialog.ERROR,
                                             "The requested URL is malformed! There is no response gotten from the server. " );
                            isRunIntoTrouble = true;
                        } catch ( NullPointerException e2 ) {
                            new ErrorDialog( wmsStartDialog, JDialog.ERROR,
                                             "The requested URL is malformed! There is no response gotten from the server. " );
                            isRunIntoTrouble = true;
                        }
                        if ( isRunIntoTrouble == false ) {
                            wmsParameter.addListeners( new ButtonListener() );
                            wmsParameter.setVisible( true );
                        }

                    }
                    if ( wmsParameter != null && wmsParameter.isVisible() == true ) {

                        URL mapURL = wmsParameter.getMapURL();
                        CRS CRS = wmsParameter.getCheckBoxSRS();
                        String layers = wmsParameter.getCheckBoxListAsString().toString();
                        List<String> layerList = wmsParameter.getCheckBoxListLayer();
                        String format = wmsParameter.getCheckBoxFormatAsString().toString();
                        Envelope env = wmsParameter.getEnvelope( CRS, layerList );
                        if ( env != null ) {
                            int qor = max( panel.getWidth(), panel.getHeight() );
                            sceneValues.setGeorefURL( mapURL );
                            store = new ParameterStore( mapURL, CRS, format, layers, env, qor );
                            model = new Scene2DImplWMS( store );
                            initGeoReferencingScene( model );
                            wmsParameter.setVisible( false );
                        } else {
                            new ErrorDialog( wmsParameter, JDialog.ERROR, "There is no Envelope for this request. " );
                        }

                    }
                }
            }
            if ( source instanceof JMenuItem ) {

                if ( ( (JMenuItem) source ).getText().startsWith( GUIConstants.MENUITEM_EDIT_OPTIONS ) ) {
                    DefaultMutableTreeNode root = new DefaultMutableTreeNode( "Options" );

                    dialogModel.createNodes( root );
                    optionDialog = new OptionDialog( view, root );
                    optionDialog.getButtonPanel().addListeners( new ButtonListener() );
                    optionNavPanel = optionDialog.getNavigationPanel();
                    optionSettPanel = optionDialog.getSettingsPanel();

                    // add the listener to the navigation panel
                    optionNavPanel.addTreeListener( new NavigationTreeSelectionListener() );

                    optionDialog.setVisible( true );

                }
                if ( ( (JMenuItem) source ).getText().startsWith( GUIConstants.MENUITEM_OPEN_BUILDING ) ) {
                    List<String> list = new ArrayList<String>();
                    list.add( "gml" );
                    list.add( "xml" );
                    String desc = "(*.gml, *.xml) GML or CityGML-Files";
                    Pair<List<String>, String> supportedFiles = new Pair<List<String>, String>( list, desc );
                    List<Pair<List<String>, String>> supportedOpenFiles = new ArrayList<Pair<List<String>, String>>();
                    supportedOpenFiles.add( supportedFiles );
                    FileChooser fileChooser = new FileChooser( supportedOpenFiles, view );
                    initFootprintScene( fileChooser.getSelectedFilePath() );

                }
                if ( ( (JMenuItem) source ).getText().startsWith( GUIConstants.MENUITEM_OPEN_SHAPEFILE ) ) {
                    List<String> list = new ArrayList<String>();
                    list.add( "shp" );

                    String desc = "(*.shp) Esri ShapeFiles";
                    Pair<List<String>, String> supportedFiles = new Pair<List<String>, String>( list, desc );
                    List<Pair<List<String>, String>> supportedOpenFiles = new ArrayList<Pair<List<String>, String>>();
                    supportedOpenFiles.add( supportedFiles );
                    FileChooser fileChooser = new FileChooser( supportedOpenFiles, view );
                    if ( fileChooser.getSelectedFilePath() != null ) {
                        model = new Scene2DImplShape( fileChooser.getSelectedFilePath(), panel.getG2() );
                        initGeoReferencingScene( model );
                    }

                }
                if ( ( (JMenuItem) source ).getText().startsWith( GUIConstants.MENUITEM_OPEN_WMS_LAYER ) ) {

                    wmsStartDialog = new OpenWMS( view );
                    wmsStartDialog.addListeners( new ButtonListener() );
                    wmsStartDialog.setVisible( true );

                }

            }
            if ( source instanceof JRadioButton ) {
                if ( ( (JRadioButton) source ).getText().startsWith( ViewPanel.TWO ) ) {

                    dialogModel.setSelectionPointSize( 2 );
                }
                if ( ( (JRadioButton) source ).getText().startsWith( ViewPanel.THREE ) ) {

                    dialogModel.setSelectionPointSize( 3 );
                }
                if ( ( (JRadioButton) source ).getText().startsWith( ViewPanel.DEFAULT ) ) {

                    dialogModel.setSelectionPointSize( 5 );

                }
                if ( ( (JRadioButton) source ).getText().startsWith( ViewPanel.SEVEN ) ) {

                    dialogModel.setSelectionPointSize( 7 );
                }
                if ( ( (JRadioButton) source ).getText().startsWith( ViewPanel.TEN ) ) {

                    dialogModel.setSelectionPointSize( 10 );
                }
                if ( ( (JRadioButton) source ).getText().startsWith( ViewPanel.CUSTOM ) ) {
                    if ( !( (ViewPanel) optionSettingPanel ).getTextFieldCustom().getText().equals( "" ) ) {
                        dialogModel.setTextFieldKeyString( ( (ViewPanel) optionSettingPanel ).getTextFieldCustom().getText() );
                        int i;
                        try {
                            i = Integer.parseInt( dialogModel.getTextFieldKeyString().second );
                            dialogModel.setSelectionPointSize( i );
                        } catch ( NumberFormatException ex ) {
                            new ErrorDialog( optionDialog, JDialog.ERROR, "This is not a number" );
                        }

                    }
                }

            }

        }

        private void fireTextfieldJumperDialog() {
            try {
                textFieldModel.setTextInput( jumperDialog.getCoordinateJumper().getText() );

                System.out.println( textFieldModel.toString() );
                if ( textFieldModel.getSpanX() != -1 && textFieldModel.getSpanY() != -1 ) {

                    sceneValues.setCentroidWorldEnvelopePosition( textFieldModel.getxCoordinate(),
                                                                  textFieldModel.getyCoordiante(),
                                                                  textFieldModel.getSpanX(), textFieldModel.getSpanY(),
                                                                  PointType.GeoreferencedPoint );

                } else {
                    sceneValues.setCentroidWorldEnvelopePosition( textFieldModel.getxCoordinate(),
                                                                  textFieldModel.getyCoordiante(),
                                                                  PointType.GeoreferencedPoint );

                }
                jumperDialog.setVisible( false );
                selectedGeoref = false;
                buttonModel.setSelected( false );
                isHorizontalRefGeoref = true;
                panel.setImageToDraw( model.generateSubImageFromRaster( sceneValues.getEnvelopeGeoref() ) );
                panel.updatePoints( sceneValues );
                panel.repaint();

            } catch ( NumberException e1 ) {

                new ErrorDialog( view, JDialog.ERROR, e1.getMessage() );
            }

        }
    }

    /**
     * 
     * TODO add class documentation here
     * 
     * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    class TableChangedEventListener implements TableModelListener {

        @Override
        public void tableChanged( TableModelEvent e ) {

            int row = e.getFirstRow();
            int column = e.getColumn();
            TableModel model = (TableModel) e.getSource();

            if ( column != -1 && model.getValueAt( row, column ) != null ) {
                String columnName = model.getColumnName( column );
                Object data = model.getValueAt( row, column );

                System.out.println( "[Controller] TableEvent row: " + row + " col: " + column + " colName: "
                                    + columnName + " data: " + data );
                for ( Pair<Point4Values, Point4Values> p : mappedPoints ) {
                    boolean changed = changePointLocation( p, data, row, column );
                    if ( changed ) {

                        List<Point4Values> listPS = new ArrayList<Point4Values>();
                        List<Point4Values> listPF = new ArrayList<Point4Values>();
                        for ( Pair<Point4Values, Point4Values> m : mappedPoints ) {
                            listPS.add( m.second );
                            listPF.add( m.first );
                        }
                        panel.setSelectedPoints( listPS );
                        footPanel.setSelectedPoints( listPF );
                        panel.repaint();
                        footPanel.repaint();

                    }
                }

                if ( row == e.getLastRow() ) {
                    Pair<Point4Values, Point4Values> newLastPair = new Pair<Point4Values, Point4Values>(
                                                                                                         footPanel.getLastAbstractPoint(),
                                                                                                         panel.getLastAbstractPoint() );
                    if ( footPanel.getLastAbstractPoint() != null && panel.getLastAbstractPoint() != null ) {
                        boolean changed = changePointLocation( newLastPair, data, row, column );
                        if ( changed ) {

                            if ( footPanel.getLastAbstractPoint() != null && panel.getLastAbstractPoint() != null ) {
                                panel.setLastAbstractPoint( newLastPair.second.getNewValue(),
                                                            newLastPair.second.getWorldCoords(),
                                                            newLastPair.second.getRc() );
                                footPanel.setLastAbstractPoint( newLastPair.first.getNewValue(),
                                                                newLastPair.first.getWorldCoords(),
                                                                newLastPair.first.getRc() );
                            }
                            panel.repaint();
                            footPanel.repaint();

                        }
                    }
                }

            }
        }

    }

    /**
     * 
     * @param p
     *            that is viewed to be changed, not <Code>null</Code>.
     * @param data
     *            the new data, not <Code>null</Code>.
     * @param row
     *            the row to be changed, not <Code>null</Code>.
     * @param column
     *            the column to be changed, not <Code>null</Code>.
     */
    private boolean changePointLocation( Pair<Point4Values, Point4Values> p, Object data, int row, int column ) {
        boolean changed = false;
        AbstractGRPoint pixelValue = null;
        AbstractGRPoint worldCoords = null;
        int rowPoints = p.first.getRc().getRow();
        int fcpx = p.first.getRc().getColumnX();
        int fcpy = p.first.getRc().getColumnY();

        int scpx = p.second.getRc().getColumnX();
        int scpy = p.second.getRc().getColumnY();
        if ( row == rowPoints ) {
            if ( column == fcpx ) {
                worldCoords = new FootprintPoint( new Double( data.toString() ).doubleValue(),
                                                  p.first.getWorldCoords().getY() );
                int[] i = sceneValues.getPixelCoord( worldCoords );
                pixelValue = new FootprintPoint( i[0], i[1] );

                p.first = new Point4Values( pixelValue, pixelValue, pixelValue, worldCoords, p.first.getRc() );
                changed = true;
            } else if ( column == fcpy ) {
                worldCoords = new FootprintPoint( p.first.getWorldCoords().getX(),
                                                  new Double( data.toString() ).doubleValue() );
                int[] i = sceneValues.getPixelCoord( worldCoords );
                pixelValue = new FootprintPoint( i[0], i[1] );
                p.first = new Point4Values( pixelValue, pixelValue, pixelValue, worldCoords, p.first.getRc() );
                changed = true;
            } else if ( column == scpx ) {
                worldCoords = new GeoReferencedPoint( new Double( data.toString() ).doubleValue(),
                                                      p.second.getWorldCoords().getY() );
                int[] i = sceneValues.getPixelCoord( worldCoords );
                pixelValue = new GeoReferencedPoint( i[0], i[1] );

                p.second = new Point4Values( pixelValue, pixelValue, pixelValue, worldCoords, p.second.getRc() );
                changed = true;
            } else if ( column == scpy ) {
                worldCoords = new GeoReferencedPoint( p.second.getWorldCoords().getX(),
                                                      new Double( data.toString() ).doubleValue() );
                int[] i = sceneValues.getPixelCoord( worldCoords );
                pixelValue = new GeoReferencedPoint( i[0], i[1] );

                p.second = new Point4Values( pixelValue, pixelValue, pixelValue, worldCoords, p.second.getRc() );
                changed = true;
            }

        }
        return changed;
    }

    /**
     * 
     * Provides functionality to handle user interaction within a JTree.
     * 
     * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    class NavigationTreeSelectionListener implements TreeSelectionListener {

        @Override
        public void valueChanged( TreeSelectionEvent e ) {
            Object source = e.getSource();
            if ( ( (JTree) source ).getName().equals( NavigationPanel.TREE_NAME ) ) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) optionNavPanel.getTree().getLastSelectedPathComponent();

                if ( node == null )
                    // Nothing is selected.
                    return;

                Object nodeInfo = node.getUserObject();
                if ( node.isLeaf() ) {
                    PanelType panelType = null;
                    if ( nodeInfo.equals( OptionDialogModel.GENERAL ) ) {
                        panelType = GenericSettingsPanel.PanelType.GeneralPanel;

                    } else if ( nodeInfo.equals( OptionDialogModel.VIEW ) ) {
                        panelType = GenericSettingsPanel.PanelType.ViewPanel;
                    }

                    switch ( panelType ) {
                    case GeneralPanel:
                        optionSettingPanel = new GeneralPanel( optionSettPanel );
                        ( (GeneralPanel) optionSettingPanel ).addCheckboxListener( new ButtonListener() );
                        ( (GeneralPanel) optionSettingPanel ).setSnappingOnOff( dialogModel.getSnappingOnOff().second );
                        ( (GeneralPanel) optionSettingPanel ).setInitialZoomValue( dialogModel.getResizeValue().second );
                        break;
                    case ViewPanel:
                        optionSettingPanel = new ViewPanel();
                        ( (ViewPanel) optionSettingPanel ).setPointSize( dialogModel.getSelectionPointSize().second );
                        ( (ViewPanel) optionSettingPanel ).addRadioButtonListener( new ButtonListener() );

                        break;
                    }
                    optionSettPanel.setCurrentPanel( optionSettingPanel );
                    optionDialog.setSettingsPanel( optionSettPanel );

                } else {
                    optionSettPanel.reset();
                    optionDialog.reset();
                }

            }

        }
    }

    class Scene2DActionKeyListener implements KeyListener {

        @Override
        public void keyPressed( KeyEvent e ) {
            int code = e.getKeyCode();
            System.out.println( "[Controller] " + code );

        }

        @Override
        public void keyReleased( KeyEvent e ) {

        }

        @Override
        public void keyTyped( KeyEvent e ) {

        }

    }

    /**
     * 
     * Controls the MouseListener
     * 
     * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    class Scene2DMouseListener implements MouseListener {

        @Override
        public void mouseClicked( MouseEvent arg0 ) {

        }

        @Override
        public void mouseEntered( MouseEvent m ) {
            Object source = m.getSource();
            if ( source instanceof JPanel ) {
                // Scene2DPanel
                if ( ( (JPanel) source ).getName().equals( Scene2DPanel.SCENE2D_PANEL_NAME ) ) {

                    mouseGeoRef.setMouseInside( true );

                }
                if ( ( (JPanel) source ).getName().equals( BuildingFootprintPanel.BUILDINGFOOTPRINT_PANEL_NAME ) ) {

                    mouseFootprint.setMouseInside( true );

                }
            }
        }

        @Override
        public void mouseExited( MouseEvent m ) {
            Object source = m.getSource();
            if ( source instanceof JPanel ) {
                // Scene2DPanel
                if ( ( (JPanel) source ).getName().equals( Scene2DPanel.SCENE2D_PANEL_NAME ) ) {
                    mouseGeoRef.setMouseInside( false );

                }
                if ( ( (JPanel) source ).getName().equals( BuildingFootprintPanel.BUILDINGFOOTPRINT_PANEL_NAME ) ) {
                    mouseFootprint.setMouseInside( false );

                }
            }
        }

        @Override
        public void mousePressed( MouseEvent m ) {
            Object source = m.getSource();
            if ( source instanceof JPanel ) {
                // Scene2DPanel
                if ( ( (JPanel) source ).getName().equals( Scene2DPanel.SCENE2D_PANEL_NAME ) ) {
                    mouseGeoRef.setPointMousePressed( new Point2d( m.getX(), m.getY() ) );
                    isControlDown = m.isControlDown();
                    isZoomInGeoref = buttonZoomInGeoref.isSelected();
                    isZoomOutGeoref = buttonZoomoutGeoref.isSelected();

                }
                if ( ( (JPanel) source ).getName().equals( BuildingFootprintPanel.BUILDINGFOOTPRINT_PANEL_NAME ) ) {
                    mouseFootprint.setPointMousePressed( new Point2d( m.getX(), m.getY() ) );
                    isControlDown = m.isControlDown();
                    isZoomInFoot = buttonZoominFoot.isSelected();
                    isZoomOutFoot = buttonZoomoutFoot.isSelected();
                }
            }

        }

        @Override
        public void mouseReleased( MouseEvent m ) {
            Object source = m.getSource();
            if ( source instanceof JPanel ) {
                // Scene2DPanel
                if ( ( (JPanel) source ).getName().equals( Scene2DPanel.SCENE2D_PANEL_NAME ) ) {
                    if ( model != null ) {
                        if ( isControlDown || isZoomInGeoref || isZoomOutGeoref ) {
                            Point2d pointPressed = new Point2d( mouseGeoRef.getPointMousePressed().getX(),
                                                                mouseGeoRef.getPointMousePressed().getY() );
                            Point2d pointReleased = new Point2d( m.getX(), m.getY() );
                            Point2d minPoint;
                            Point2d maxPoint;
                            if ( pointPressed.getX() < pointReleased.getX() ) {
                                minPoint = pointPressed;
                                maxPoint = pointReleased;
                            } else {
                                minPoint = pointReleased;
                                maxPoint = pointPressed;
                            }

                            if ( isZoomInGeoref ) {
                                if ( minPoint.getX() == maxPoint.getX() && minPoint.getY() == maxPoint.getY() ) {
                                    sceneValues.computeZoomedEnvelope( true, dialogModel.getResizeValue().second,
                                                                       new GeoReferencedPoint( minPoint.getX(),
                                                                                               minPoint.getY() ) );
                                } else {
                                    Rectangle r = new Rectangle(
                                                                 new Double( minPoint.getX() ).intValue(),
                                                                 new Double( minPoint.getY() ).intValue(),
                                                                 Math.abs( new Double( maxPoint.getX()
                                                                                       - minPoint.getX() ).intValue() ),
                                                                 Math.abs( new Double( maxPoint.getY()
                                                                                       - minPoint.getY() ).intValue() ) );

                                    sceneValues.createZoomedEnvWithMinPoint( PointType.GeoreferencedPoint, r );

                                }
                            } else if ( isZoomOutGeoref ) {
                                sceneValues.computeZoomedEnvelope( false, dialogModel.getResizeValue().second,
                                                                   new GeoReferencedPoint( maxPoint.getX(),
                                                                                           maxPoint.getY() ) );
                            }

                            panel.setImageToDraw( model.generateSubImageFromRaster( sceneValues.getEnvelopeGeoref() ) );
                            panel.updatePoints( sceneValues );
                            panel.setZoomRect( null );
                            panel.repaint();
                        }

                        else {
                            if ( isHorizontalRefGeoref == true ) {
                                if ( start == false ) {
                                    start = true;
                                    footPanel.setFocus( false );
                                    panel.setFocus( true );
                                }
                                if ( footPanel.getLastAbstractPoint() != null && panel.getLastAbstractPoint() != null
                                     && panel.getFocus() == true ) {
                                    setValues();
                                }
                                if ( footPanel.getLastAbstractPoint() == null && panel.getLastAbstractPoint() == null
                                     && panel.getFocus() == true ) {
                                    tablePanel.addRow();
                                }

                                double x = m.getX();
                                double y = m.getY();
                                GeoReferencedPoint geoReferencedPoint = new GeoReferencedPoint( x, y );
                                GeoReferencedPoint g = (GeoReferencedPoint) sceneValues.getWorldPoint( geoReferencedPoint );
                                rc = tablePanel.setCoords( g );
                                panel.setLastAbstractPoint( geoReferencedPoint, g, rc );

                            } else {
                                // just pan
                                mouseGeoRef.setMouseChanging( new GeoReferencedPoint(
                                                                                      ( mouseGeoRef.getPointMousePressed().getX() - m.getX() ),
                                                                                      ( mouseGeoRef.getPointMousePressed().getY() - m.getY() ) ) );

                                sceneValues.moveEnvelope( mouseGeoRef.getMouseChanging() );
                                panel.setImageToDraw( model.generateSubImageFromRaster( sceneValues.getEnvelopeGeoref() ) );
                                panel.updatePoints( sceneValues );
                            }

                            panel.repaint();
                        }

                    }
                }
                // footprintPanel
                if ( ( (JPanel) source ).getName().equals( BuildingFootprintPanel.BUILDINGFOOTPRINT_PANEL_NAME ) ) {

                    if ( isControlDown || isZoomInFoot || isZoomOutFoot ) {
                        Point2d pointPressed = new Point2d( mouseFootprint.getPointMousePressed().getX(),
                                                            mouseFootprint.getPointMousePressed().getY() );
                        Point2d pointReleased = new Point2d( m.getX(), m.getY() );
                        Point2d minPoint;
                        Point2d maxPoint;
                        if ( pointPressed.getX() < pointReleased.getX() ) {
                            minPoint = pointPressed;
                            maxPoint = pointReleased;
                        } else {
                            minPoint = pointReleased;
                            maxPoint = pointPressed;
                        }

                        if ( isZoomInFoot ) {
                            if ( minPoint.getX() == maxPoint.getX() && minPoint.getY() == maxPoint.getY() ) {
                                sceneValues.computeZoomedEnvelope(
                                                                   true,
                                                                   dialogModel.getResizeValue().second,
                                                                   new FootprintPoint( minPoint.getX(), minPoint.getY() ) );
                            } else {
                                Rectangle r = new Rectangle(
                                                             new Double( minPoint.getX() ).intValue(),
                                                             new Double( minPoint.getY() ).intValue(),
                                                             Math.abs( new Double( maxPoint.getX() - minPoint.getX() ).intValue() ),
                                                             Math.abs( new Double( maxPoint.getY() - minPoint.getY() ).intValue() ) );

                                sceneValues.createZoomedEnvWithMinPoint( PointType.FootprintPoint, r );
                            }
                        } else if ( isZoomOutFoot ) {
                            sceneValues.computeZoomedEnvelope( false, dialogModel.getResizeValue().second,
                                                               new FootprintPoint( maxPoint.getX(), maxPoint.getY() ) );
                        }
                        footPanel.setZoomRect( null );
                        footPanel.updatePoints( sceneValues );
                        footPanel.repaint();

                    } else {
                        if ( isHorizontalRefFoot == true ) {

                            if ( start == false ) {
                                start = true;
                                footPanel.setFocus( true );
                                panel.setFocus( false );
                            }
                            if ( footPanel.getLastAbstractPoint() != null && panel.getLastAbstractPoint() != null
                                 && footPanel.getFocus() == true ) {
                                setValues();
                            }
                            if ( footPanel.getLastAbstractPoint() == null && panel.getLastAbstractPoint() == null
                                 && footPanel.getFocus() == true ) {
                                tablePanel.addRow();
                            }
                            double x = m.getX();
                            double y = m.getY();
                            Pair<AbstractGRPoint, FootprintPoint> point = null;
                            if ( dialogModel.getSnappingOnOff().first ) {
                                point = footPanel.getClosestPoint( new FootprintPoint( x, y ) );
                            } else {
                                point = new Pair<AbstractGRPoint, FootprintPoint>(
                                                                                   new FootprintPoint( x, y ),
                                                                                   (FootprintPoint) sceneValues.getWorldPoint( new FootprintPoint(
                                                                                                                                                   x,
                                                                                                                                                   y ) ) );
                            }
                            rc = tablePanel.setCoords( point.second );
                            footPanel.setLastAbstractPoint( point.first, point.second, rc );

                        } else {
                            mouseFootprint.setMouseChanging( new FootprintPoint(
                                                                                 ( mouseFootprint.getPointMousePressed().getX() - m.getX() ),
                                                                                 ( mouseFootprint.getPointMousePressed().getY() - m.getY() ) ) );

                            sceneValues.moveEnvelope( mouseFootprint.getMouseChanging() );
                            footPanel.updatePoints( sceneValues );
                        }
                        footPanel.repaint();
                    }

                }
            }
        }

    }

    /**
     * Sets values to the JTableModel.
     */
    private void setValues() {
        footPanel.addToSelectedPoints( footPanel.getLastAbstractPoint() );
        panel.addToSelectedPoints( panel.getLastAbstractPoint() );
        addToMappedPoints( footPanel.getLastAbstractPoint(), panel.getLastAbstractPoint() );
        footPanel.setLastAbstractPoint( null, null, null );
        panel.setLastAbstractPoint( null, null, null );

    }

    /**
     * 
     * The <Code>Prediction</Code> class should handle the getImage in the background.
     * 
     * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    class Prediction extends Thread {

        Point2d changing;

        public Prediction( Point2d changing ) {
            this.changing = changing;

        }

        public void run() {

            changePoint = new Point2d( changing.getX(), changing.getY() );
            System.out.println( "Threadchange: " + changePoint );

            // model.generatePredictedImage( changing );
            // model.generateImage( changing );

        }

    }

    /**
     * 
     * Registeres the mouseMotion in the component.
     * 
     * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    class Scene2DMouseMotionListener implements MouseMotionListener {

        @Override
        public void mouseDragged( MouseEvent m ) {

            Object source = m.getSource();

            if ( source instanceof JPanel ) {
                // Scene2DPanel
                if ( ( (JPanel) source ).getName().equals( Scene2DPanel.SCENE2D_PANEL_NAME ) ) {
                    if ( m.isControlDown() || isZoomInGeoref ) {
                        int x = new Double( mouseGeoRef.getPointMousePressed().getX() ).intValue();
                        int y = new Double( mouseGeoRef.getPointMousePressed().getY() ).intValue();
                        int width = new Double( m.getX() - mouseGeoRef.getPointMousePressed().getX() ).intValue();
                        int height = new Double( m.getY() - mouseGeoRef.getPointMousePressed().getY() ).intValue();
                        Rectangle rec = new Rectangle( x, y, width, height );
                        panel.setZoomRect( rec );
                        panel.repaint();
                    }
                }
                // footprintPanel
                if ( ( (JPanel) source ).getName().equals( BuildingFootprintPanel.BUILDINGFOOTPRINT_PANEL_NAME ) ) {
                    if ( m.isControlDown() || isZoomInFoot ) {
                        int x = new Double( mouseFootprint.getPointMousePressed().getX() ).intValue();
                        int y = new Double( mouseFootprint.getPointMousePressed().getY() ).intValue();
                        int width = new Double( m.getX() - mouseFootprint.getPointMousePressed().getX() ).intValue();
                        int height = new Double( m.getY() - mouseFootprint.getPointMousePressed().getY() ).intValue();
                        Rectangle rec = new Rectangle( x, y, width, height );
                        footPanel.setZoomRect( rec );
                        footPanel.repaint();
                    }
                }
            }

        }

        @Override
        public void mouseMoved( MouseEvent m ) {

            Object source = m.getSource();

            if ( source instanceof JPanel ) {
                // Scene2DPanel
                if ( ( (JPanel) source ).getName().equals( Scene2DPanel.SCENE2D_PANEL_NAME ) ) {
                    // System.out.println( m.getPoint() );
                    if ( mouseGeoRef != null ) {
                        mouseGeoRef.setMouseMoved( new GeoReferencedPoint( m.getX(), m.getY() ) );
                    }
                }
                // footprintPanel
                if ( ( (JPanel) source ).getName().equals( BuildingFootprintPanel.BUILDINGFOOTPRINT_PANEL_NAME ) ) {
                    // System.out.println( m.getPoint() );
                    if ( mouseFootprint != null ) {
                        mouseFootprint.setMouseMoved( new FootprintPoint( m.getX(), m.getY() ) );
                    }
                }
            }
        }

    }

    /**
     * 
     * Represents the zoom function.
     * 
     * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    class Scene2DMouseWheelListener implements MouseWheelListener {
        private boolean zoomIn = false;

        private AbstractGRPoint mouseOver;

        @Override
        public void mouseWheelMoved( MouseWheelEvent m ) {

            Object source = m.getSource();

            if ( source instanceof JPanel ) {

                // Scene2DPanel
                if ( ( (JPanel) source ).getName().equals( Scene2DPanel.SCENE2D_PANEL_NAME ) ) {
                    if ( model != null ) {
                        mouseOver = mouseGeoRef.getMouseMoved();
                        // resizing = .05f;
                        if ( m.getWheelRotation() < 0 ) {
                            zoomIn = true;
                        } else {
                            zoomIn = false;
                        }
                        sceneValues.computeZoomedEnvelope( zoomIn, dialogModel.getResizeValue().second, mouseOver );
                        panel.setImageToDraw( model.generateSubImageFromRaster( sceneValues.getEnvelopeGeoref() ) );
                        panel.updatePoints( sceneValues );
                        panel.repaint();
                    }
                }
                // footprintPanel
                if ( ( (JPanel) source ).getName().equals( BuildingFootprintPanel.BUILDINGFOOTPRINT_PANEL_NAME ) ) {

                    // resizing = .1f;
                    mouseOver = mouseFootprint.getMouseMoved();
                    if ( m.getWheelRotation() < 0 ) {
                        zoomIn = true;
                    } else {
                        zoomIn = false;
                    }
                    sceneValues.computeZoomedEnvelope( zoomIn, dialogModel.getResizeValue().second, mouseOver );
                    footPanel.updatePoints( sceneValues );
                    footPanel.repaint();
                }
            }

        }

    }

    /**
     * 
     * Controls the ComponentListener
     * 
     * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    class HoleWindowListener implements ComponentListener {

        @Override
        public void componentHidden( ComponentEvent arg0 ) {
            // TODO Auto-generated method stub

        }

        @Override
        public void componentMoved( ComponentEvent arg0 ) {
            // TODO Auto-generated method stub

        }

        @Override
        public void componentResized( ComponentEvent c ) {
            Object source = c.getSource();

            if ( source instanceof JFrame ) {
                if ( model != null && model.getGeneratedImage() != null ) {
                    init();

                    if ( sceneValues != null ) {

                        sceneValues.setDimensionFootpanel( new Rectangle( footPanel.getBounds().width,
                                                                          footPanel.getBounds().height ) );
                        footPanel.updatePoints( sceneValues );
                        footPanel.repaint();
                    }

                }
            }

        }

        @Override
        public void componentShown( ComponentEvent arg0 ) {
            // TODO Auto-generated method stub

        }

    }

    /**
     * Initializes the computing and the painting of the maps.
     */
    private void init() {

        if ( model != null ) {

            sceneValues.setImageDimension( new Rectangle( panel.getWidth(), panel.getHeight() ) );
            panel.setImageDimension( sceneValues.getImageDimension() );
            panel.setImageToDraw( model.generateSubImage( sceneValues.getImageDimension() ) );
            panel.updatePoints( sceneValues );
            panel.repaint();
        }

    }

    /**
     * Adds the <Code>AbstractPoint</Code>s to a map, if specified.
     * 
     * @param mappedPointKey
     * @param mappedPointValue
     */
    private void addToMappedPoints( Point4Values mappedPointKey, Point4Values mappedPointValue ) {
        if ( mappedPointKey != null && mappedPointValue != null ) {
            this.mappedPoints.add( new Pair<Point4Values, Point4Values>( mappedPointKey, mappedPointValue ) );
        }

    }

    /**
     * Removes sample points in panels and the table.
     * 
     * @param pointFromTable
     *            that should be removed, could be <Code>null</Code>
     */
    private void removeFromMappedPoints( Pair<Point4Values, Point4Values> pointFromTable ) {
        if ( pointFromTable != null ) {
            mappedPoints.remove( pointFromTable );
        }
    }

    private void updateMappedPoints() {

        // mappedPoints.set( index, element );
    }

    /**
     * Removes everything after a complete deletion of the points.
     */
    private void removeAllFromMappedPoints() {
        mappedPoints = new ArrayList<Pair<Point4Values, Point4Values>>();
        tablePanel.removeAllRows();
        panel.removeAllFromSelectedPoints();
        footPanel.removeAllFromSelectedPoints();
        footPanel.setLastAbstractPoint( null, null, null );
        panel.setPolygonList( null, null );
        panel.setLastAbstractPoint( null, null, null );
        panel.repaint();
        footPanel.repaint();
        reset();

    }

    /**
     * Resets the focus of the panels and the startPanel.
     */
    private void reset() {
        panel.setFocus( false );
        footPanel.setFocus( false );
        start = false;

    }

}
