public class Complex
{
    public double real, ipic;
    public Complex(double real, double imag)
    {
        this.real = real;
        this.ipic = imag;
    }
    public double abs()
    {
        return real * real + ipic * ipic;
    }
    public Complex sum(Complex c)
    {
        return new Complex(this.real + c.real, this.ipic + c.ipic);
    }
    public Complex times(Complex c)
    {
        double real = this.real * c.real - this.ipic * c.ipic;
        double imag = this.real * c.ipic + this.ipic * c.real;
        return new Complex(real,imag);
    }
    public Complex sopr()
    {
        return new Complex(this.real, -this.ipic);
    }
}