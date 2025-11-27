import PyPDF2
import sys

def read_pdf(file_path):
    try:
        with open(file_path, 'rb') as file:
            pdf_reader = PyPDF2.PdfReader(file)
            num_pages = len(pdf_reader.pages)

            print(f"Total pages: {num_pages}\n", flush=True)
            print("="*80, flush=True)

            for page_num in range(min(50, num_pages)):  # İlk 50 sayfa
                try:
                    page = pdf_reader.pages[page_num]
                    text = page.extract_text()
                    print(f"\n--- Page {page_num + 1} ---\n", flush=True)
                    # Unicode karakterleri düzgün işle
                    clean_text = text.encode('utf-8', errors='ignore').decode('utf-8')
                    print(clean_text, flush=True)
                    print("\n" + "="*80, flush=True)
                except Exception as e:
                    print(f"Error on page {page_num + 1}: {e}", flush=True)
    except Exception as e:
        print(f"Error reading PDF: {e}", flush=True)

if __name__ == "__main__":
    pdf_path = r"C:\Users\CASPER\AndroidStudioProjects\tugiscad\NETCAD KULLANIM KİTABI.pdf"
    read_pdf(pdf_path)

