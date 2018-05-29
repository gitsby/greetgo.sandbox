export class Client {

    public snmn: string;
    public character: string;
    public age: number;
    public accBalance: number = 10;
    public maxBalance: number = 10;
    public minBalance: number = 10;

    public assign(o: any): Client {
        this.snmn = o.surname;
        return this;
    }


    public static copy(a: any): Client {
        let ret = new Client();
        ret.assign(a);
        return ret;
    }
}