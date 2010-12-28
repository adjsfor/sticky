package com.google.appengine.demos.sticky.client;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.google.appengine.demos.sticky.client.model.Model;
import com.google.appengine.demos.sticky.client.model.Note;
import com.google.appengine.demos.sticky.client.model.Photo;
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

public class PhotoTransformView extends PopupPanel {

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
	
	private Note note;
    
    private Model model;
		
	public PhotoTransformView(Model model, Note note) {
		
			// PopupPanel's constructor takes 'auto-hide' as its boolean
			// parameter.
			// If this is set, the panel closes itself automatically when the
			// user
			// clicks outside of it.
			super(true);
			this.model = model;
			this.note = note;

			// PopupPanel is a SimplePanel, so you have to set it's widget
			// property to
			// whatever you want its contents to be.
			setWidget(new Label("Click outside of this popup to close it"));

			final Element elem = getElement();
			elem.setId("image-panel");
			final Images images = GWT.create(Images.class);
			final Photo photo = this.note.getPhoto();
			
			add(Buttons.createPushButtonWithImageStates(images
					.headerImageCrop().createImage(), "crop-button",
					new ClickHandler() {
						public void onClick(ClickEvent event) {
							
							// TODO crop image
							double leftX = 0.;
							double topY = 0.;
							double rightX = 0.;
							double bottomY = 0.;
							// TODO get blobkey from associated picture
							BlobKey blobKey;
							ImagesService imagesService = ImagesServiceFactory
									.getImagesService();
							Image oldImage = ImagesServiceFactory
									.makeImageFromBlob(blobKey);
							Transform transform = ImagesServiceFactory
									.makeCrop(leftX, topY, rightX, bottomY);
							Image newImage = imagesService.applyTransform(
									transform, oldImage);
							byte[] newImageData = newImage.getImageData();
							// TODO store newImageData
						}
					}));

			add(Buttons.createPushButtonWithImageStates(images
					.headerImageFlipH().createImage(), "flip-h-button",
					new ClickHandler() {
						public void onClick(ClickEvent event) {
							// TODO flip image horizontally
							// TODO get blobkey from associated picture
							BlobKey blobKey;
							ImagesService imagesService = ImagesServiceFactory
									.getImagesService();
							Image oldImage = ImagesServiceFactory
									.makeImageFromBlob(blobKey);
							Transform transform = ImagesServiceFactory
									.makeHorizontalFlip();
							Image newImage = imagesService.applyTransform(
									transform, oldImage);
							byte[] newImageData = newImage.getImageData();
							// TODO store newImageData
						}
					}));

			add(Buttons.createPushButtonWithImageStates(images
					.headerImageFlipV().createImage(), "flip-v-button",
					new ClickHandler() {
						public void onClick(ClickEvent event) {
							// TODO flip image vertically
							// TODO get blobkey from associated picture
							BlobKey blobKey;
							ImagesService imagesService = ImagesServiceFactory
									.getImagesService();
							Image oldImage = ImagesServiceFactory
									.makeImageFromBlob(blobKey);
							Transform transform = ImagesServiceFactory
									.makeVerticalFlip();
							Image newImage = imagesService.applyTransform(
									transform, oldImage);
							byte[] newImageData = newImage.getImageData();
							// TODO store newImageData
						}
					}));

			add(Buttons.createPushButtonWithImageStates(images
					.headerImageRotateC().createImage(), "rotate-c-button",
					new ClickHandler() {
						public void onClick(ClickEvent event) {
							// TODO rotate image clockwise
							// TODO get blobkey from associated picture
							BlobKey blobKey;
							ImagesService imagesService = ImagesServiceFactory
									.getImagesService();
							Image oldImage = ImagesServiceFactory
									.makeImageFromBlob(blobKey);
							Transform transform = ImagesServiceFactory
									.makeRotate(90);
							Image newImage = imagesService.applyTransform(
									transform, oldImage);
							byte[] newImageData = newImage.getImageData();
							// TODO store newImageData
						}
					}));

			add(Buttons.createPushButtonWithImageStates(images
					.headerImageRotateCc().createImage(), "rotate-cc-button",
					new ClickHandler() {
						public void onClick(ClickEvent event) {
							// TODO rotate image counter clockwise
							// TODO get blobkey from associated picture
							BlobKey blobKey;
							ImagesService imagesService = ImagesServiceFactory
									.getImagesService();
							Image oldImage = ImagesServiceFactory
									.makeImageFromBlob(blobKey);
							Transform transform = ImagesServiceFactory
									.makeRotate(-90);
							Image newImage = imagesService.applyTransform(
									transform, oldImage);
							byte[] newImageData = newImage.getImageData();
							// TODO store newImageData
						}
					}));
		}
	}
/*
	public void onModuleLoad() {
		Button b1 = new Button("Click me to show popup");
		b1.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// Instantiate the popup and show it.
				new ImageTransformView().show();
			}
		});

		RootPanel.get().add(b1);

		Button b2 = new Button(
				"Click me to show popup partway across the screen");

		b2.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// Create the new popup.
				final MyPopup popup = new MyPopup();
				// Position the popup 1/3rd of the way down and across the
				// screen, and
				// show the popup. Since the position calculation is based on
				// the
				// offsetWidth and offsetHeight of the popup, you have to use
				// the
				// setPopupPositionAndShow(callback) method. The alternative
				// would
				// be to call show(), calculate the left and top positions, and
				// call setPopupPosition(left, top). This would have the ugly
				// side
				// effect of the popup jumping from its original position to its
				// new position.
				popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
					public void setPosition(int offsetWidth, int offsetHeight) {
						int left = (Window.getClientWidth() - offsetWidth) / 3;
						int top = (Window.getClientHeight() - offsetHeight) / 3;
						popup.setPopupPosition(left, top);
					}
				});
			}
		});

		RootPanel.get().add(b2);
	}
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
	 * .headerImageRotateCc().createImage(), "rotate-cc-button", new
	 * ClickHandler() { public void onClick(ClickEvent event) { // TODO rotate
	 * image counter clockwise
	 * 
	 * } })); }
	 */

}