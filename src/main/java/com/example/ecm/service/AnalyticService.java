package com.example.ecm.service;

import com.example.ecm.dto.responses.*;
import com.example.ecm.exception.NotFoundException;
import com.example.ecm.model.SignatureRequest;
import com.example.ecm.repository.*;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.index.mapper.TimeSeriesParams;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticService {

    private final SignatureRequestRepository signatureRequestRepository;
    private final VotingRepository votingRepository;
    private final DocumentRepository documentRepository;
    private final AnalyticRepository analyticRepository;
    private final UserRepository userRepository;


    public List<UserApproval> getApprovalsByUsers(LocalDateTime startDate, LocalDateTime endDate) {
        return signatureRequestRepository.findApprovalsByUsers(startDate, endDate);
    }

    //public List<VotingSummary> getVotingSummaries(LocalDateTime startDate, LocalDateTime endDate) {
   //     return votingRepository.findVotingSummaries(startDate, endDate);
   // }

    public List<ActiveUserProjection> getMostActiveUsers(LocalDateTime startDate, LocalDateTime endDate) {
        return documentRepository.findMostActiveUsers(startDate, endDate);
    }

    public List<IgnoredVotes> getIgnoredVotes(LocalDateTime startDate, LocalDateTime endDate) {
        return signatureRequestRepository.findIgnoredVotes(startDate, endDate);
    }

    public List<SignatureStatus> getCountSignatureRequestStatus() {
        return signatureRequestRepository.findCountSignatureRequestStatus();
    }

    public List<UserSignaturesSummary> getUsersSignaturesSummary() {
        List<SignatureRequest> signatureRequests = signatureRequestRepository.findAll();

        return signatureRequests.stream()
                .collect(Collectors.groupingBy(
                        signatureRequest -> signatureRequest.getUserTo().getId()
                ))
                .entrySet().stream()
                .map(entry -> {
                    Long userId = entry.getKey();
                    var signatureRequestId2status = entry.getValue().stream()
                            .collect(Collectors.toMap(
                                    SignatureRequest::getId,
                                    SignatureRequest::getStatusString
                            ));

                    return new UserSignaturesSummary(userId, signatureRequestId2status.entrySet().size(), signatureRequestId2status);
                })
                .toList();
    }

    public List<DocumentSignatureRequestStatistics> getDocumentSignatureRequestStatistics() {
        return signatureRequestRepository.findDocumentSignatureRequestStatistics();
    }

    /**
     * Количество документов по типам.
     * @return Список DocumentCountByTypeRequest с информацией о количестве документов по каждому типу
     */
    public List<DocumentCountByTypeResponse> getDocumentCountByType() {
        return analyticRepository.countDocumentsByType();
    }

    /**
     * Количество активных и неактивных документов.
     *
     * @return Список DocumentStatusCountResponse с информацией о количестве активных и неактивных документов
     */
    public List<DocumentStatusCountResponse> getDocumentStatusCounts() {
        return analyticRepository.countDocumentsByStatus();
    }

    /**
     * Процентное соотношение документов по типам.
     *
     * @return Список DocumentTypePercentageResponse с процентным соотношением документов по каждому типу
     */
    public List<DocumentTypePercentageResponse> getDocumentTypePercentages() {
        List<DocumentCountByTypeResponse> documentCountByType = analyticRepository.countDocumentsByType();
        List<DocumentTypePercentageResponse> documentTypePercentageResponses = new ArrayList<>();
        Long countType = 0L;
        for (DocumentCountByTypeResponse documentCountByTypeResponse : documentCountByType) {
            countType += documentCountByTypeResponse.getDocumentCount();
        }
        for (DocumentCountByTypeResponse documentCountByTypeResponse : documentCountByType) {
            DocumentTypePercentageResponse documentTypePercentageResponse = new DocumentTypePercentageResponse(documentCountByTypeResponse.getDocumentType(), documentCountByTypeResponse.getDocumentCount() * 100.0 / countType);
            documentTypePercentageResponses.add(documentTypePercentageResponse);
        }
        return documentTypePercentageResponses;
    }




    private void clearDirectory(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        clearDirectory(file);
                    }
                    file.delete();
                }
            }
        }
    }

    public String generateInfographicReport(LocalDateTime startDate, LocalDateTime endDate) throws Exception {
        // Aggregate data
        List<UserApproval> approvals = getApprovalsByUsers(startDate, endDate);
        List<DailyApprovalStats> dailyStats = signatureRequestRepository.findDailyApprovals(startDate, endDate);
       // List<VotingSummary> votings = getVotingSummaries(startDate, endDate);
        List<ActiveUserProjection> activeUsers = getMostActiveUsers(startDate, endDate);
        List<IgnoredVotes> ignoredVotes = getIgnoredVotes(startDate, endDate);
        List<SignatureStatus> signatureStatuses = getCountSignatureRequestStatus();
        List<DocumentCountByTypeResponse> docCounts = getDocumentCountByType();
        List<DocumentStatusCountResponse> docStatusCounts = getDocumentStatusCounts();
        List<DocumentTypePercentageResponse> docTypePercentages = getDocumentTypePercentages();
        List<UserSignaturesSummary> userSignaturesSummaries = getUsersSignaturesSummary();
        List<DocumentSignatureRequestStatistics> docSignatureStats = getDocumentSignatureRequestStatistics();

        // Generate PDF
        File tmpDir = new File("tmp");
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        clearDirectory(tmpDir);

        // Generate PDF path with current date and time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String formattedDateTime = now.format(formatter);
        String pdfPath = "tmp/AnalyticsReport_" + formattedDateTime + ".pdf";
        DateTimeFormatter formatByDate = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

        PdfWriter writer = new PdfWriter(pdfPath);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        Div div = new Div();
        div.setTextAlignment(TextAlignment.CENTER);
        div.setWidth(UnitValue.createPercentValue(100));

        // Создание параграфа с заголовком
        Paragraph title = new Paragraph("Analytics Report")
                .setFontSize(36)
                .setTextAlignment(TextAlignment.CENTER);

        // Создание параграфа с датой
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatDate = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

        Paragraph period = new Paragraph(startDate.format(formatDate) + " - " + endDate.format(formatDate))
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER);

        Paragraph date = new Paragraph("Report Date: " + currentDate.format(formatDate))
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER);

        document.add(title);
        document.add(period);
        document.add(date);

        // Title
        // document.add(new Paragraph("Analytics Report").setFontSize(20));

//        document.add(new Paragraph("1. Approvals").setFontSize(16));
//        BufferedImage approvalsChart = createBarChart(approvals);
//        addChartToDocument(approvalsChart, document);



        document.add(new Paragraph("1. Daily Approvals").setFontSize(16));
        BufferedImage dailyChartImage = createDailyApprovalsChart(dailyStats);
        addChartToDocument(dailyChartImage, document);

        // Section: Voting Summaries
        document.add(new AreaBreak());
        document.add(new Paragraph("2. Voting Summaries").setFontSize(16));
       // div.add(createTableFromVotingSummaries(votings));
        document.add(div);

        // Section: Active Users
        document.add(new AreaBreak());
        document.add(new Paragraph("3. Active Users").setFontSize(16));
        Table activeUsersTable = createTableFromActiveUsers(activeUsers);
        Div div2 = new Div();
        div2.add(activeUsersTable);
        div2.setTextAlignment(TextAlignment.CENTER);
        div2.setWidth(UnitValue.createPercentValue(100));
        document.add(div2);

        // Section: Ignored Votes
        document.add(new AreaBreak());
        document.add(new Paragraph("4. Ignored Votes").setFontSize(16));
        BufferedImage ignoredVotesChart = createIgnoredVotesBarChart(ignoredVotes);
        addChartToDocument(ignoredVotesChart, document);

        // Section: Signature Status
        document.add(new AreaBreak());
        document.add(new Paragraph("5. Signature Request Status").setFontSize(16));
        BufferedImage signatureStatusChart = createPieChartFromSignatureStatus(signatureStatuses);
        addChartToDocument(signatureStatusChart, document);

        // Section: Document Counts by Type
        document.add(new AreaBreak());
        document.add(new Paragraph("6. Document Count by Type").setFontSize(16));
        BufferedImage documentCountChart = createBarChartFromDocCounts(docCounts);
        addChartToDocument(documentCountChart, document);

        // Section: Document Status Counts
        document.add(new AreaBreak());
        document.add(new Paragraph("7. Document Status Counts").setFontSize(16));
        BufferedImage documentStatusChart = createPieChartFromDocStatusCounts(docStatusCounts);
        addChartToDocument(documentStatusChart, document);

        document.add(new AreaBreak());
        document.add(new Paragraph("8. Document Type Percentages").setFontSize(16));
        BufferedImage docTypePercentagesChart = createPieChart(docTypePercentages);
        addChartToDocument(docTypePercentagesChart, document);

        // Section: User Signatures Summary
        document.add(new AreaBreak());
        document.add(new Paragraph("9. User Signatures Summary").setFontSize(16));
        document.add(createTableFromUserSignaturesSummaries(userSignaturesSummaries));

        // Section: Document Signature Request Statistics
        document.add(new AreaBreak());
        document.add(new Paragraph("10. Document Signature Request Statistics").setFontSize(16));
        BufferedImage docSignatureStatsChart = createStackedBarChart(docSignatureStats);
        addChartToDocument(docSignatureStatsChart, document);

        // Close document
        document.close();
        return pdfPath;
    }

    private BufferedImage createDailyApprovalsChart(List<DailyApprovalStats> dailyStats) {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (DailyApprovalStats stats : dailyStats) {
            String dayLabel = String.format("%d.%d", stats.getMonth(), stats.getDay());

            // Add cumulative count to the chart
            dataset.addValue(stats.getCumulativeCount(), "Cumulative Approvals", dayLabel);

            // Add the daily approval count
            dataset.addValue(stats.getApprovalsCount(), "Daily Approvals", dayLabel);

            // Add the daily growth
            dataset.addValue(stats.getDailyGrowth(), "Daily Growth", dayLabel);
        }


        // Create the chart
        JFreeChart chart = ChartFactory.createLineChart(
                "Daily Approved Documents with Growth", // Chart title
                "Day", // X-axis label
                "Number of Approvals", // Y-axis label
                dataset, // Dataset
                PlotOrientation.VERTICAL, // Orientation of the chart
                true, // Include legend
                true, // Tooltips
                false // URLs
        );

        // Customize chart appearance
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);

        // Customize the appearance of the lines
        plot.getRenderer().setSeriesPaint(0, Color.BLUE); // Total Approvals (blue line)
        plot.getRenderer().setSeriesPaint(1, Color.RED);  // Daily Growth (red line)

        // Return the chart as a BufferedImage
        return chart.createBufferedImage(800, 600);
    }



    private BufferedImage createWeeklyApprovalsChart(List<WeeklyApprovalStats> weeklyStats) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Populate dataset
        for (WeeklyApprovalStats stats : weeklyStats) {
            String weekLabel = String.format("Year %d, Week %d", stats.getYear(), stats.getWeek());
            dataset.addValue(stats.getApprovalCount(), "Approved", weekLabel);
        }

        // Create the chart
        JFreeChart chart = ChartFactory.createLineChart(
                "Weekly Approved Documents",
                "Week",
                "Number of Approvals",
                dataset
        );

        // Customize chart appearance
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);

        return chart.createBufferedImage(800, 600);
    }


    private Table createTableFromVotingSummaries(List<VotingSummary> votings) {
        Table table = new Table(new float[]{2, 4, 2, 3, 2, 2, 2});
        table.addHeaderCell("Document Version ID");
        table.addHeaderCell("Document Title");
        table.addHeaderCell("Participant Count");
        table.addHeaderCell("Participants");
        table.addHeaderCell("Voting Status");
        table.addHeaderCell("Approval Threshold");
        table.addHeaderCell("Current Approval Rate");

        for (VotingSummary voting : votings) {
            table.addCell(String.valueOf(voting.getDocumentVersionId()));
            table.addCell(voting.getDocumentTitle());
            table.addCell(String.valueOf(voting.getParticipantCount()));
            table.addCell(voting.getParticipants());
            table.addCell(voting.getVotingStatus());
            table.addCell(String.format("%.2f", voting.getApprovalThreshold()));
            table.addCell(String.format("%.2f", voting.getCurrentApprovalRate()));
        }
        return table;
    }

    private Table createTableFromActiveUsers(List<ActiveUserProjection> activeUsers) {
        Table table = new Table(new float[]{3, 2});
        table.addHeaderCell("User");
        table.addHeaderCell("Documents Created");

        for (ActiveUserProjection userProjection : activeUsers) {
            table.addCell(userProjection.getUser().getName()); // Adjust based on User object fields
            table.addCell(String.valueOf(userProjection.getDocumentsCreated()));
        }
        return table;
    }

    private BufferedImage createIgnoredVotesBarChart(List<IgnoredVotes> ignoredVotes) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (IgnoredVotes vote : ignoredVotes) {
            String userName = vote.getUserName();
            Long ignoredCount = vote.getIgnoredVoteCount();

            dataset.addValue(ignoredCount, "Ignored Votes", userName);
        }

        // Создаем график
        JFreeChart chart = ChartFactory.createBarChart(
                "Ignored Votes by User",
                "User",
                "Counts",
                dataset
        );

        chart.setBackgroundPaint(Color.WHITE);

        return chart.createBufferedImage(600, 400);
    }


    private BufferedImage createBarChartFromDocCounts(List<DocumentCountByTypeResponse> docCounts) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (DocumentCountByTypeResponse response : docCounts) {
            dataset.addValue(response.getDocumentCount(), "Document Count", response.getDocumentType());
        }
        JFreeChart chart = ChartFactory.createBarChart(
                "Document Count by Type",
                "Document Type",
                "Document Count",
                dataset
        );
        return chart.createBufferedImage(600, 400);
    }


    private BufferedImage createPieChartFromDocStatusCounts(List<DocumentStatusCountResponse> docStatusCounts) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (DocumentStatusCountResponse response : docStatusCounts) {
            String statusLabel = response.getIsAlive() ? "Active" : "Inactive";
            dataset.setValue(statusLabel, response.getDocumentCount());
        }
        JFreeChart chart = ChartFactory.createPieChart(
                "Document Status Counts",
                dataset,
                true,
                true,
                false
        );
        return chart.createBufferedImage(600, 400);
    }

    private Table createTableFromUserSignaturesSummaries(List<UserSignaturesSummary> summaries) {
        Table table = new Table(new float[]{2, 2, 6});
        table.addHeaderCell("Username");
        table.addHeaderCell("Signature Requests Count");
        table.addHeaderCell("Signature Request Status");

        for (UserSignaturesSummary summary : summaries) {
            table.addCell(userRepository.findById(summary.getUserId()).get().getSurname());
            table.addCell(String.valueOf(summary.getSignatureRequestsCount()));
            table.addCell(summary.getSignatureRequestId2status().values().toString());
        }
        return table;
    }


    private BufferedImage createStackedBarChart(List<DocumentSignatureRequestStatistics> stats) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (DocumentSignatureRequestStatistics stat : stats) {
            com.example.ecm.model.Document document = documentRepository.findById(stat.getDocumentId())
                    .orElse(new com.example.ecm.model.Document());

            String documentName = "";
            if (!document.getDocumentVersions().isEmpty())
                documentName = document
                        .getDocumentVersions()
                        .getLast()
                        .getTitle();
            dataset.addValue(stat.getRequestCount(), "Requests", documentName);
            dataset.addValue(stat.getApprovedCount(), "Approved", documentName);
            dataset.addValue(stat.getIgnoredCount(), "Ignored", documentName);
            dataset.addValue(stat.getPendingCount(), "Pending", documentName);
            dataset.addValue(stat.getRejectedCount(), "Rejected", documentName);
        }

        JFreeChart chart = ChartFactory.createStackedBarChart(
                "Document Signature Request Statistics",
                "Document title",
                "Counts",
                dataset
        );

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        // Assign unique colors to each series
        renderer.setSeriesPaint(0, new Color(102, 102, 255)); // Requests
        renderer.setSeriesPaint(1, new Color(102, 255, 102)); // Approved
        renderer.setSeriesPaint(2, new Color(255, 153, 51)); // Ignored
        renderer.setSeriesPaint(3, new Color(255, 255, 102)); // Pending
        renderer.setSeriesPaint(4, new Color(255, 102, 102)); // Rejected

        return chart.createBufferedImage(600, 400);
    }


    private BufferedImage createPieChartFromSignatureStatus(List<SignatureStatus> signatureStatuses) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (SignatureStatus status : signatureStatuses) {
            dataset.setValue(status.getStatus(), status.getRequestCount());
        }
        JFreeChart chart = ChartFactory.createPieChart(
                "Signature Request Status Distribution",
                dataset,
                true,
                true,
                false
        );
        return chart.createBufferedImage(600, 400);
    }



    private BufferedImage createPieChart(List<DocumentTypePercentageResponse> docTypePercentages) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (DocumentTypePercentageResponse response : docTypePercentages) {
            dataset.setValue(response.getDocumentType(), response.getDocumentPercentage());
        }
        JFreeChart chart = ChartFactory.createPieChart(
                "Document Types",
                dataset,
                true,
                true,
                false
        );
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("Type A", new Color(255, 102, 102));
        plot.setSectionPaint("Type B", new Color(102, 178, 255));
        plot.setSectionPaint("Type C", new Color(178, 255, 102)); // Additional types as needed
        return chart.createBufferedImage(600, 400);
    }

    private BufferedImage createBarChart(List<UserApproval> approvals) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Проходим по всем данным UserApproval
        for (UserApproval approval : approvals) {
            String documentLabel = approval.getDocumentTitle(); // Название документа
            Long approvalCount = approval.getApprovalCount(); // Количество подтверждений
            Long ignoredCount = approval.getApprovalType().equals("Ignored") ? approvalCount : 0; // Количество игнорированных
            Long pendingCount = approval.getApprovalType().equals("Pending") ? approvalCount : 0; // Количество ожидающих


            // Добавляем данные в dataset
            dataset.addValue(approvalCount, "Approved", documentLabel);
            dataset.addValue(ignoredCount, "Ignored", documentLabel);
            dataset.addValue(pendingCount, "Pending", documentLabel);
        }

        // Создаем график
        JFreeChart chart = ChartFactory.createBarChart(
                "Document Signature Request Statistics", // Заголовок графика
                "Document Name",                         // Метка оси X
                "Counts",                                // Метка оси Y
                dataset                                  // Данные для графика
        );

        // Возвращаем изображение
        return chart.createBufferedImage(600, 400);
    }

    private void addChartToDocument(BufferedImage chartImage, Document document) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(chartImage, "PNG", baos);
        byte[] imageData = baos.toByteArray();
        Image chart = new Image(ImageDataFactory.create(imageData));
        document.add(chart);
    }
}
