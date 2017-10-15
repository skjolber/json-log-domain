package com.github.skjolber.log.domain.codegen;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.github.skjolber.log.domain.model.Domain;
import com.github.skjolber.log.domain.model.Key;
import com.github.skjolber.log.domain.model.Tag;

import net.steppschuh.markdowngenerator.list.UnorderedList;
import net.steppschuh.markdowngenerator.table.Table;
import net.steppschuh.markdowngenerator.text.Text;
import net.steppschuh.markdowngenerator.text.code.CodeBlock;
import net.steppschuh.markdowngenerator.text.heading.Heading;

public class MarkdownGenerator {

	public static void generate(Path file, Path outputFile, boolean javaCodeGenerated) throws IOException {
		Domain domain = DomainFactory.parse(Files.newBufferedReader(file, StandardCharsets.UTF_8));

		generate(domain, outputFile, javaCodeGenerated);
	}

	public static void generate(Domain domain, Path outputFile, boolean javaCodeGenerated) throws IOException {
		Writer writer = Files.newBufferedWriter(outputFile);
		try {
			writer.write(generate(domain, javaCodeGenerated));
		} finally {
			writer.close();
		}
	}
	
	public static String generate(Domain domain, boolean javaCodeGenerated) throws IOException {
		StringBuilder sb = new StringBuilder();
				
		sb.append(new Heading(domain.getName(), 2));
		
		if(domain.getDescription() != null) {
			sb.append("\n");
			sb.append(new Text(domain.getDescription()));
		}
		sb.append("\n");
		sb.append("\n");
		
		List<String> items = new ArrayList<>();
		items.add("Version: " + domain.getVersion());
		items.add("Package: " + domain.getTargetPackage());
		if(domain.getQualifier() != null && !domain.getQualifier().isEmpty()) {
			items.add("Qualifier: " + domain.getQualifier());
		}
		
		sb.append(new UnorderedList<>(items));
		sb.append("\n");

		sb.append("\n");
		if(domain.getQualifier() == null || domain.getQualifier().isEmpty()) {
			sb.append(new Text("Logged items will appear in global scope."));
			sb.append("\n");
		}
		sb.append("\n");
		sb.append(new Heading("Keys", 3));
		sb.append("\n");
		sb.append("\n");

		Table.Builder tableViewer = new Table.Builder()
				.withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT, Table.ALIGN_LEFT)
				.addRow("Key", "Name", "Type", "Description");
		
		for(Key key : domain.getKeys()) {
			if(key.getFormat() != null) {
				tableViewer = tableViewer.addRow(key.getId(), key.getName(), key.getType() + "/" + key.getFormat(), key.getDescription());
			} else {
				tableViewer = tableViewer.addRow(key.getId(), key.getName(), key.getType(), key.getDescription());
			}
		}

		sb.append(tableViewer.build());
		sb.append("\n");
		sb.append("\n");
		if(javaCodeGenerated) {
			sb.append(new Text("Add the following import:"));
			sb.append("\n");
			sb.append("\n");
			String code = String.format("import static %1s.%2s.*;", domain.getTargetPackage(), domain.getName() + MarkerGenerator.MARKER_BUILDER);
			
			sb.append(new CodeBlock(code, "java"));
			sb.append("\n");
			sb.append("\n");
		}
		
		if(domain.hasTags()) {
			sb.append(new Heading("Tags", 3));
			sb.append("\n");
			sb.append("\n");
	
			tableViewer = new Table.Builder()
					.withAlignments(Table.ALIGN_LEFT, Table.ALIGN_LEFT)
					.addRow("Tag", "Description");
			for(Tag tag : domain.getTags()) {
				tableViewer = tableViewer.addRow(tag.getId(), tag.getDescription());
			}
	
			sb.append(tableViewer.build());
			sb.append("\n");
			sb.append("\n");
			if(javaCodeGenerated) {
				sb.append(new Text("Add the following import:"));
				sb.append("\n");
				sb.append("\n");
				String tagCode = String.format("import static %1s.%2s.*;", domain.getTargetPackage(), domain.getName() + TagGenerator.TAG);
				sb.append(new CodeBlock(tagCode, "java"));
				sb.append("\n");
				sb.append("\n");
			}
		}
		
		return sb.toString();
	}

}
