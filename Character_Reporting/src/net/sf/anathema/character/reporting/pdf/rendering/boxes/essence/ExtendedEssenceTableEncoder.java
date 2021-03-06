package net.sf.anathema.character.reporting.pdf.rendering.boxes.essence;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import net.sf.anathema.character.generic.character.IGenericCharacter;
import net.sf.anathema.character.generic.character.IGenericTraitCollection;
import net.sf.anathema.character.reporting.pdf.content.essence.ExtendedEssenceContent;
import net.sf.anathema.character.reporting.pdf.content.essence.pools.PoolRow;
import net.sf.anathema.character.reporting.pdf.content.essence.recovery.RecoveryRow;
import net.sf.anathema.character.reporting.pdf.rendering.extent.Bounds;
import net.sf.anathema.character.reporting.pdf.rendering.extent.Position;
import net.sf.anathema.character.reporting.pdf.rendering.general.table.ITableEncoder;
import net.sf.anathema.character.reporting.pdf.rendering.general.table.TableEncodingUtilities;
import net.sf.anathema.character.reporting.pdf.rendering.general.traits.PdfTraitEncoder;
import net.sf.anathema.character.reporting.pdf.rendering.graphics.GraphicsTemplate;
import net.sf.anathema.character.reporting.pdf.rendering.graphics.SheetGraphics;
import net.sf.anathema.character.reporting.pdf.rendering.graphics.TableCell;

import java.util.List;

import static com.itextpdf.text.Element.ALIGN_CENTER;
import static com.itextpdf.text.Element.ALIGN_MIDDLE;
import static com.itextpdf.text.Element.ALIGN_RIGHT;
import static net.sf.anathema.character.reporting.pdf.rendering.general.traits.PdfTraitEncoder.DOT_PADDING;
import static net.sf.anathema.character.reporting.pdf.rendering.page.IVoidStateFormatConstants.BARE_LINE_HEIGHT;
import static net.sf.anathema.character.reporting.pdf.rendering.page.IVoidStateFormatConstants.TEXT_PADDING;

public class ExtendedEssenceTableEncoder implements ITableEncoder<ExtendedEssenceContent> {
  protected static float PADDING = 1f;
  protected static float DOTS_WIDTH = 130f;

  private final PdfTraitEncoder traitEncoder = PdfTraitEncoder.createLargeTraitEncoder();

  private static final int HEADER_BORDER = Rectangle.NO_BORDER;
  private static final int INTERNAL_BORDER = Rectangle.BOX;
  private static final int LABEL_BORDER = Rectangle.BOX;

  protected Float[] getEssenceColumns() {
    return new Float[]{8f, 2f, 2f, 2f };
  }

  private float[] createColumnWidth() {
    return net.sf.anathema.lib.lang.ArrayUtilities.toPrimitive(getEssenceColumns());
  }

  public final float getTableHeight(ExtendedEssenceContent content, float width) throws DocumentException {
    int lines = content.getOverallLineCount();
    return traitEncoder.getTraitHeight() + lines * BARE_LINE_HEIGHT + 1.75f * TEXT_PADDING;
  }

  @Override
  public final float encodeTable(SheetGraphics graphics, ExtendedEssenceContent content, Bounds bounds) throws DocumentException {
    PdfPTable table = createTable(graphics, content);
    table.setWidthPercentage(100);
    graphics.createSimpleColumn(bounds).withElement(table).encode();
    return table.getTotalHeight();
  }

  protected IGenericTraitCollection getTraits(IGenericCharacter character) {
    return character.getTraitCollection();
  }

  protected final PdfPTable createTable(SheetGraphics graphics, ExtendedEssenceContent content) throws DocumentException {
    float[] columnWidth = createColumnWidth();
    PdfPTable table = new PdfPTable(columnWidth);
    addEssenceHeader(graphics, table, createDots(graphics, content, DOTS_WIDTH), content);
    addEssencePoolRows(graphics, table, content);
    return table;
  }

  protected final Image createDots(SheetGraphics graphics, ExtendedEssenceContent content, float width) throws BadElementException {
    GraphicsTemplate template = graphics.createTemplate(width, traitEncoder.getTraitHeight());
    int value = content.getEssenceValue();
    traitEncoder.encodeDotsCenteredAndUngrouped(template.getTemplateGraphics(), new Position(0, DOT_PADDING), width, value, content.getEssenceMax());
    return template.getImageInstance();
  }

  private final void addEssenceHeader(SheetGraphics graphics, PdfPTable table, Image firstCell, ExtendedEssenceContent content) {
    PdfPCell spaceCell = createSpaceCell(graphics);
    Font font = getDefaultFont(graphics);
    TableCell headerCell = new TableCell(firstCell);
    headerCell.setColspan(4);
    table.addCell(headerCell);

    table.addCell(spaceCell);
    table.addCell(createHeaderCell(font, content.getEssenceTotalHeaderLabel(), 1));
    table.addCell(createHeaderCell(font, content.getCommittedHeaderLabel(), 1));
    table.addCell(createHeaderCell(font, content.getAvailableHeaderLabel(), 1));
  }

  private void addEssencePoolRows(SheetGraphics graphics, PdfPTable table, ExtendedEssenceContent content) {
    List<PoolRow> poolRows = content.getPoolRows();
    for (int index = 0; index < content.getNumberOfContentLines(); index++) {
      addPoolCells(getDefaultFont(graphics), table, poolRows.get(index));
    }
  }

  private TableCell markCell(RecoveryRow recoveryRow, TableCell cell) {
    if (recoveryRow.isMarked()) {
      cell.setBorderWidth(cell.getBorderWidthTop() * 2f);
    }
    return cell;
  }

  private void addPoolCells(Font font, PdfPTable table, PoolRow poolRow) {
    Phrase labelPhrase = new Phrase(poolRow.getLabel(), font);
    table.addCell(new TableCell(labelPhrase, LABEL_BORDER, ALIGN_CENTER, ALIGN_MIDDLE));

    Phrase capacityPhrase = new Phrase(poolRow.getCapacity(), font);
    table.addCell(new TableCell(capacityPhrase, INTERNAL_BORDER, ALIGN_CENTER, ALIGN_MIDDLE));

    Phrase committedPhrase = new Phrase(poolRow.getCommitted(), font);
    PdfPCell committedCell = new TableCell(committedPhrase, INTERNAL_BORDER, ALIGN_CENTER, ALIGN_MIDDLE);
    if (!poolRow.isCommitmentEnabled()) {
      committedCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
    }
    table.addCell(committedCell);
    Phrase availablePhrase = new Phrase(poolRow.getAvailable(), font);
    table.addCell(new TableCell(availablePhrase, INTERNAL_BORDER, ALIGN_RIGHT, ALIGN_MIDDLE));
  }

  protected final PdfPCell createHeaderCell(Font font, String text, int columnSpan) {
    PdfPCell cell = new TableCell(new Phrase(text, font), HEADER_BORDER, ALIGN_CENTER, Element.ALIGN_BOTTOM);
    cell.setColspan(columnSpan);
    return cell;
  }

  private PdfPCell createSpaceCell(SheetGraphics graphics) {
    PdfPCell spaceCell = new PdfPCell(new Phrase(" ", getDefaultFont(graphics))); //$NON-NLS-1$
    spaceCell.setBorder(HEADER_BORDER);
    return spaceCell;
  }

  private final Font getDefaultFont(SheetGraphics graphics) {
    return graphics.createTableFont();
  }

  private final Font getBoldFont(SheetGraphics graphics) {
    return TableEncodingUtilities.createBoldTableFont(graphics.getBaseFont());
  }

  public boolean hasContent(ExtendedEssenceContent content) {
    return content.hasContent();
  }
}
