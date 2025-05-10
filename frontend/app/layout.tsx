import { ReactNode } from "react";
import "./globals.css";

export default function RootLayout({
    children,
}: Readonly<{
    children: ReactNode;
}>) {
    return (
        <html>
            <body>
                {children}
            </body>
        </html>
    );
}
