/*
 *  Copyright 2007-2008, Plutext Pty Ltd.
 *   
 *  This file is part of docx4j.

    docx4j is licensed under the Apache License, Version 2.0 (the "License"); 
    you may not use this file except in compliance with the License. 

    You may obtain a copy of the License at 

        http://www.apache.org/licenses/LICENSE-2.0 

    Unless required by applicable law or agreed to in writing, software 
    distributed under the License is distributed on an "AS IS" BASIS, 
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
    See the License for the specific language governing permissions and 
    limitations under the License.

 */

package org.docx4j.openpackaging.parts.PresentationML;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.docx4j.XmlUtils;
import org.docx4j.dml.CTPositiveSize2D;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.relationships.Namespaces;
import org.docx4j.relationships.Relationship;
import org.pptx4j.pml.CTSlideIdList;
import org.pptx4j.pml.CTSlideIdListEntry;
import org.pptx4j.pml.CTSlideLayoutIdListEntry;
import org.pptx4j.pml.CTSlideMasterIdList;
import org.pptx4j.pml.CTSlideMasterIdListEntry;
import org.pptx4j.pml.CTSlideSize;
import org.pptx4j.pml.ObjectFactory;
import org.pptx4j.pml.Presentation;
import org.pptx4j.pml.SldMaster;



public final class MainPresentationPart extends JaxbPmlPart<Presentation> {
	
	public MainPresentationPart(PartName partName) throws InvalidFormatException {
		super(partName);
		init();
	}

	public MainPresentationPart() throws InvalidFormatException {
		super(new PartName("/ppt/presentation.xml"));
		init();
	}
	
	public void init() {		
		// Used if this Part is added to [Content_Types].xml 
		setContentType(new  org.docx4j.openpackaging.contenttype.ContentType( 
				org.docx4j.openpackaging.contenttype.ContentTypes.PRESENTATIONML_MAIN));

		// Used when this Part is added to a rels 
		setRelationshipType(Namespaces.PRESENTATIONML_MAIN);
		
	}
	
	private final static String DEFAULT_SLIDE_SIZE = "<p:sldSz xmlns:p=\"http://schemas.openxmlformats.org/presentationml/2006/main\"" +
			" cx=\"9144000\" cy=\"6858000\" type=\"screen4x3\"/>";
	
	
	private final static String DEFAULT_NOTES_SIZE = "<p:notesSz xmlns:p=\"http://schemas.openxmlformats.org/presentationml/2006/main\" " +
			"cx=\"6858000\" cy=\"9144000\"/>";

	
	public static Presentation createJaxbPresentationElement() throws JAXBException {

		ObjectFactory factory = Context.getpmlObjectFactory(); 
		Presentation presentation = factory.createPresentation();

		// Create empty lists
		CTSlideMasterIdList masterIds = factory.createCTSlideMasterIdList();
		CTSlideIdList slideIds = factory.createCTSlideIdList();		
		presentation.setSldMasterIdLst(masterIds);
		presentation.setSldIdLst(slideIds);
		
		presentation.setNotesSz( 
				(CTPositiveSize2D)XmlUtils.unmarshalString(DEFAULT_NOTES_SIZE, Context.jcPML, CTPositiveSize2D.class) );
		presentation.setSldSz(
				(CTSlideSize)XmlUtils.unmarshalString(DEFAULT_SLIDE_SIZE, Context.jcPML, CTSlideSize.class));
		
		return presentation;
	}

	public CTSlideIdListEntry addSlideIdListEntry(SlidePart slidePart) 
		throws InvalidFormatException {	

		Relationship rel = this.addTargetPart(slidePart);
		
		CTSlideIdListEntry entry = Context.getpmlObjectFactory().createCTSlideIdListEntry();
		
		entry.setId( this.getSlideId() );
		entry.setRid(rel.getId());
		
		this.jaxbElement.getSldIdLst().getSldId().add(entry);
		
		return entry;
		
	}
	
	public CTSlideMasterIdListEntry addSlideMasterIdListEntry(SlideMasterPart slideMasterPart) 
		throws InvalidFormatException {	

		Relationship rel = this.addTargetPart(slideMasterPart);
		
		CTSlideMasterIdListEntry entry = Context.getpmlObjectFactory().createCTSlideMasterIdListEntry();
		
		entry.setId( new Long(this.getSlideLayoutOrMasterId()) );
		entry.setRid(rel.getId());

		this.jaxbElement.getSldMasterIdLst().getSldMasterId().add(entry);
		
		return entry;
			
		}
	
}
