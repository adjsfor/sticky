package com.google.appengine.demos.sticky.client;

import java.util.List;

import com.google.appengine.demos.sticky.client.model.Comment;
import com.google.appengine.demos.sticky.client.model.Model;
import com.google.appengine.demos.sticky.client.model.Note;
import com.google.appengine.demos.sticky.client.model.Transformation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ImageBundle;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PhotoTransformView extends HorizontalPanel {

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

		this.model = model;
		this.note = note;
		final List<Comment> comments = this.note.getComments();
		//System.out.println("Created photoTransformView!");
		if (note.getAuthorName().equals("You") && this.note.getHashCode() != null && this.note.getHashCode() != 0) {
			//System.out.println("I am the owner...");
			final Element elem = getElement();
			elem.setId("image-panel");
			final Images images = GWT.create(Images.class);

			add(Buttons.createPushButtonWithImageStates(images
					.headerImageCrop().createImage(), "crop-button",
					new ClickHandler() {
						public void onClick(ClickEvent event) {
							// TODO crop image
							model.updateNoteImage(note, 1,
									Transformation.CROP);
						}
					}));

			add(Buttons.createPushButtonWithImageStates(images
					.headerImageFlipH().createImage(), "flip-h-button",
					new ClickHandler() {
						public void onClick(ClickEvent event) {
							model.updateNoteImage(note, 1,
									Transformation.FLIP_H);
						}
					}));

			add(Buttons.createPushButtonWithImageStates(images
					.headerImageFlipV().createImage(), "flip-v-button",
					new ClickHandler() {
						public void onClick(ClickEvent event) {
							model.updateNoteImage(note, 1,
									Transformation.FLIP_V);
						}
					}));

			add(Buttons.createPushButtonWithImageStates(images
					.headerImageRotateC().createImage(), "rotate-c-button",
					new ClickHandler() {
						public void onClick(ClickEvent event) {
							model.updateNoteImage(note, 1,
									Transformation.ROT_C);
						}
					}));

			add(Buttons.createPushButtonWithImageStates(images
					.headerImageRotateCc().createImage(), "rotate-cc-button",
					new ClickHandler() {
						public void onClick(ClickEvent event) {
							model.updateNoteImage(note, 1,
									Transformation.ROT_CC);
						}
					}));
		}
	}
}
