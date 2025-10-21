package K23CNT1.Tta.Day02.ioc;

class IOService {
    public void serve() {
        System.out.println("Service is serving");
    }
}

class IOClient {
    private IOService ioService;

    // Dùng DI để truyền vào service thay vì tự tạo nó
    public IOClient(IOService service) {
        this.ioService = service;
    }

    public void doSomething() {
        ioService.serve();
    }
}

 class IOCWithDI {
    public static void main(String[] args) {
        // Tạo đối tượng Service và truyền nó vào Client
        IOService service = new IOService();
        IOClient client = new IOClient(service);
        client.doSomething();
    }
}
