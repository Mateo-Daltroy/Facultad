
1.
  A. La inversion de control sucede en dos partes, primero se establece la inversion de control en la construccion del "new ReportProcessor(new PdfExporter())", aqui se le da el control al ReportProcessor para que llame el PdfExporter cuando lo vea apropiado (Hollywood Principle).

  B. La IoC se define porque en este escenario nosotros solo le proveemos la implementacion a la frozen spot del ReportProcessor y es este el que tiene le control de cuando llamar la implementacion particular del ReportExporter. El control del flujo lo tiene ReportProcessor y el principio de diseño es el Hollywood Principle.

2.
  A. Este diseño es de Caja Blanca, explcitamente debido a la definicion de una interfaz extendible sobre la cual podemos crear clases que la Hereden. Para hacer esto apropiadamente hay que entender como los metodos son utilizados, lo cual demanda un alto conocimiento del codigo interno del framework

  B. No entiendo esta pregunta

3.
Public class AuditLogger extends ReportExporter {
  private ReportExporter realExporter;
  
  public AuditLogger(ReportExporter exporter) {
    this.realExporter = exporter;
  }

  public void export(List<Sale> data) {
    int cantVentas = data.size();
    // enviar mensaje a consola de auditoria sobre cantVentas
    this.realExporter.export(data);
  }

  public boolean shouldInclude(Sale sale) {
    return this.realExporter.shouldInclude(sale);
  }
}
// una instanciacion podria verse como:
// ReportProcessor reporter = new ReportProcessor(new AuditLogger(new PdfExporter()));
