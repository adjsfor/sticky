package com.google.appengine.demos.sticky.client;

import java.util.List;

import com.google.appengine.demos.sticky.client.model.Comment;
import com.google.appengine.demos.sticky.client.model.Model;
import com.google.appengine.demos.sticky.client.model.Note;
import com.google.appengine.demos.sticky.client.model.Photo;
import com.google.appengine.demos.sticky.client.model.Surface;
import com.google.appengine.demos.sticky.client.model.Transformation;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ImageBundle;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PhotoTransformView extends VerticalPanel implements
		Model.DataObserver {

	/**
	 * Declaration of image bundle resources used in this widget.
	 */
	@SuppressWarnings("deprecation")
	public interface Images extends ImageBundle {
		@Resource("crop_icon.gif")
		AbstractImagePrototype headerImageCrop();

		@Resource("flip_horizontal_icon.gif")
		AbstractImagePrototype headerImageFlipH();

		@Resource("flip_vertical_icon.gif")
		AbstractImagePrototype headerImageFlipV();

		@Resource("rotate_90_c_icon.gif")
		AbstractImagePrototype headerImageRotateC();

		@Resource("rotate_90_cc_icon.gif")
		AbstractImagePrototype headerImageRotateCc();
	}

	final private Note note;

	final private Model model;

	public PhotoTransformView(final Model model, final Note note) {

		// PopupPanel's constructor takes 'auto-hide' as its boolean
		// parameter.
		// If this is set, the panel closes itself automatically when the
		// user
		// clicks outside of it.
		// super(true);
		this.model = model;
		this.note = note;
		final List<Comment> comments = this.note.getComments();

		// PopupPanel is a SimplePanel, so you have to set it's widget
		// property to
		// whatever you want its contents to be.
		// setWidget(new Label("Click outside of this popup to close it"));

		// if(note.getAuthorName().equals("You") && note.getPhoto()) {
		if (note.getAuthorName().equals("You")) {

			final Element elem = getElement();
			elem.setId("image-panel");
			final Images images = GWT.create(Images.class);
			final Photo photo = this.note.getPhoto();

			final byte[] imageData = this.note.getPhoto().getBytes();

			// final ImagesService imagesService = ImagesServiceFactory
			// .getImagesService();
			// final Image oldImage = ImagesServiceFactory.makeImage(imageData);

			add(Buttons.createPushButtonWithImageStates(images
					.headerImageCrop().createImage(), "crop-button",
					new ClickHandler() {
						public void onClick(ClickEvent event) {
							// TODO crop image
							double leftX = 0.;
							double topY = 0.;
							double rightX = 0.;
							double bottomY = 0.;
							// Transform transform = ImagesServiceFactory
							// .makeCrop(leftX, topY, rightX, bottomY);
							// Image newImage = imagesService.applyTransform(
							// transform, oldImage);
							// byte[] newImageData = newImage.getImageData();
							// TODO store newImageData
							model.updateNoteContent(note, comments,
									Transformation.CROP);
						}
					}));

			add(Buttons.createPushButtonWithImageStates(images
					.headerImageFlipH().createImage(), "flip-h-button",
					new ClickHandler() {
						public void onClick(ClickEvent event) {
							// Transform transform = ImagesServiceFactory
							// .makeHorizontalFlip();
							// Image newImage = imagesService.applyTransform(
							// transform, oldImage);
							// byte[] newImageData = newImage.getImageData();
							// TODO store newImageData
							model.updateNoteContent(note, comments,
									Transformation.FLIP_H);
						}
					}));

			add(Buttons.createPushButtonWithImageStates(images
					.headerImageFlipV().createImage(), "flip-v-button",
					new ClickHandler() {
						public void onClick(ClickEvent event) {
							// Transform transform = ImagesServiceFactory
							// .makeVerticalFlip();
							// Image newImage = imagesService.applyTransform(
							// transform, oldImage);
							// byte[] newImageData = newImage.getImageData();
							// TODO store newImageData
							model.updateNoteContent(note, comments,
									Transformation.FLIP_V);
						}
					}));

			add(Buttons.createPushButtonWithImageStates(images
					.headerImageRotateC().createImage(), "rotate-c-button",
					new ClickHandler() {
						public void onClick(ClickEvent event) {
							// TODO rotate image clockwise
							// Transform transform = ImagesServiceFactory
							// .makeRotate(90);
							// Image newImage = imagesService.applyTransform(
							// transform, oldImage);
							// byte[] newImageData = newImage.getImageData();
							// TODO store newImageData
							model.updateNoteContent(note, comments,
									Transformation.ROT_C);
						}
					}));

			add(Buttons.createPushButtonWithImageStates(images
					.headerImageRotateCc().createImage(), "rotate-cc-button",
					new ClickHandler() {
						public void onClick(ClickEvent event) {
							// TODO rotate image counter clockwise
							// Transform transform = ImagesServiceFactory
							// .makeRotate(-90);
							// Image newImage = imagesService.applyTransform(
							// transform, oldImage);
							// byte[] newImageData = newImage.getImageData();
							// TODO store newImageData
							model.updateNoteContent(note, comments,
									Transformation.ROT_CC);
						}
					}));
		}
	}

	@Override
	public void onNoteCreated(Note note) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSurfaceCreated(Surface surface) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSurfaceNotesReceived(Note[] notes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSurfaceSelected(Surface nowSelected, Surface wasSelected) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSurfacesReceived(Surface[] surfaces) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCommentAdded(Comment comment) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPhotoTransform(Photo photo, Transformation transformation) {
		// TODO Auto-generated method stub

	}
}
/*
 * public void onModuleLoad() { Button b1 = new
 * Button("Click me to show popup"); b1.addClickHandler(new ClickHandler() {
 * public void onClick(ClickEvent event) { // Instantiate the popup and show it.
 * new ImageTransformView().show(); } });
 * 
 * RootPanel.get().add(b1);
 * 
 * Button b2 = new Button( "Click me to show popup partway across the screen");
 * 
 * b2.addClickHandler(new ClickHandler() { public void onClick(ClickEvent event)
 * { // Create the new popup. final MyPopup popup = new MyPopup(); // Position
 * the popup 1/3rd of the way down and across the // screen, and // show the
 * popup. Since the position calculation is based on // the // offsetWidth and
 * offsetHeight of the popup, you have to use // the //
 * setPopupPositionAndShow(callback) method. The alternative // would // be to
 * call show(), calculate the left and top positions, and // call
 * setPopupPosition(left, top). This would have the ugly // side // effect of
 * the popup jumping from its original position to its // new position.
 * popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() { public void
 * setPosition(int offsetWidth, int offsetHeight) { int left =
 * (Window.getClientWidth() - offsetWidth) / 3; int top =
 * (Window.getClientHeight() - offsetHeight) / 3; popup.setPopupPosition(left,
 * top); } }); } });
 * 
 * RootPanel.get().add(b2); }
 */
/*
 * public ImagePanel(Images images, FlowPanel headerView) {
 * headerView.add(this);
 * 
 * final Element elem = getElement(); elem.setId("image-panel");
 * add(Buttons.createPushButtonWithImageStates(images.headerImageCrop()
 * .createImage(), "crop-button", new ClickHandler() { public void
 * onClick(ClickEvent event) { // TODO crop image
 * 
 * } }));
 * 
 * add(Buttons.createPushButtonWithImageStates(images.headerImageRotateC()
 * .createImage(), "rotate-c-button", new ClickHandler() { public void
 * onClick(ClickEvent event) { // TODO rotate image clockwise
 * 
 * } }));
 * 
 * add(Buttons.createPushButtonWithImageStates(images
 * .headerImageRotateCc().createImage(), "rotate-cc-button", new ClickHandler()
 * { public void onClick(ClickEvent event) { // TODO rotate image counter
 * clockwise
 * 
 * } })); }
 * 
 * 
 * }
 */