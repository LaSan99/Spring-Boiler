package com.example.crmTelco.service;

import com.example.crmTelco.entity.Package;
import com.example.crmTelco.entity.User;
import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
public class InvoiceService {

    public byte[] generateInvoicePdf(Package packageEntity, User user) throws IOException {
        try {
            String htmlContent = generateInvoiceHtml(packageEntity, user);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            HtmlConverter.convertToPdf(htmlContent, outputStream);
            
            byte[] pdfBytes = outputStream.toByteArray();
            System.out.println("Generated PDF size: " + pdfBytes.length + " bytes");
            
            return pdfBytes;
        } catch (Exception e) {
            System.err.println("Error generating PDF: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to generate PDF invoice", e);
        }
    }

    private String generateInvoiceHtml(Package packageEntity, User user) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<title>Invoice - " + packageEntity.getInvoiceNumber() + "</title>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 40px; }" +
                ".header { text-align: center; margin-bottom: 30px; }" +
                ".invoice-details { margin-bottom: 30px; }" +
                ".customer-details, .package-details { margin-bottom: 20px; }" +
                "table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }" +
                "th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }" +
                "th { background-color: #f2f2f2; }" +
                ".total { font-weight: bold; font-size: 18px; }" +
                ".footer { margin-top: 50px; text-align: center; font-size: 12px; color: #666; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='header'>" +
                "<h1>TELECOM INVOICE</h1>" +
                "<h2>Invoice Number: " + packageEntity.getInvoiceNumber() + "</h2>" +
                "</div>" +
                
                "<div class='invoice-details'>" +
                "<p><strong>Invoice Date:</strong> " + packageEntity.getCreatedAt().format(formatter) + "</p>" +
                "<p><strong>Payment Status:</strong> " + packageEntity.getPaymentStatus() + "</p>" +
                "<p><strong>Package Type:</strong> " + packageEntity.getPackageType() + "</p>" +
                "</div>" +
                
                "<div class='customer-details'>" +
                "<h3>Customer Details</h3>" +
                "<table>" +
                "<tr><th>Name</th><td>" + user.getFullName() + "</td></tr>" +
                "<tr><th>Email</th><td>" + user.getEmail() + "</td></tr>" +
                "<tr><th>Phone</th><td>" + user.getMsisdn() + "</td></tr>" +
                "<tr><th>Address</th><td>" + (user.getAddress() != null ? user.getAddress() : "N/A") + "</td></tr>" +
                "</table>" +
                "</div>" +
                
                "<div class='package-details'>" +
                "<h3>Package Details</h3>" +
                "<table>" +
                "<tr><th>Package Name</th><td>" + packageEntity.getName() + "</td></tr>" +
                "<tr><th>Description</th><td>" + packageEntity.getDescription() + "</td></tr>" +
                "<tr><th>Data Limit</th><td>" + packageEntity.getDataLimitGB() + " GB</td></tr>" +
                "<tr><th>Voice Minutes</th><td>" + packageEntity.getVoiceMinutes() + " minutes</td></tr>" +
                "<tr><th>SMS Count</th><td>" + packageEntity.getSmsCount() + "</td></tr>" +
                "<tr><th>Start Date</th><td>" + packageEntity.getStartDate().format(formatter) + "</td></tr>" +
                "<tr><th>End Date</th><td>" + packageEntity.getEndDate().format(formatter) + "</td></tr>" +
                "</table>" +
                "</div>" +
                
                "<div class='total'>" +
                "<table>" +
                "<tr><th>Total Amount</th><td>$" + packageEntity.getPrice() + "</td></tr>" +
                "</table>" +
                "</div>" +
                
                "<div class='footer'>" +
                "<p>Thank you for choosing our telecom services!</p>" +
                "<p>This is a computer-generated invoice and does not require a signature.</p>" +
                "</div>" +
                
                "</body>" +
                "</html>";
    }
}
