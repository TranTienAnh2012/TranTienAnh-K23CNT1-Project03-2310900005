package K23CNT1.Tta.Day02.ioc;

class Service {
    public void serve() {
        System.out.println("Service is serving");
    }
}

class Client {
    private Service service;

    public Client() {
        // Client tự tạo đối tượng Service
        service = new Service();
    }

    public void doSomething() {
        service.serve();
    }
}

 class NonIoC {
    public static void main(String[] args) {
        Client client = new Client();
        client.doSomething();
    }
}

