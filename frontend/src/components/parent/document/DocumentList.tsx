import DocumentItem from './DocumentItem'
import cryingDaramgi from "../../../assets/common/crying-daramgi.png"

interface Document {
  id: number;
  date: string;
  type: 'dosage' | 'absent';
  checked: boolean;
  startDate: string;
  endDate: string;
  documentId: number;
  title: string;
  details: string;
  childId: number;
}

interface DocumentListProps {
  documents: Document[];
  handleDocumentClick: (docType: string, docId: number) => void;
}

export default function DocumentList({ documents, handleDocumentClick }: DocumentListProps) {
  return (
    <div className="space-y-6">
      {documents.length === 0 ? (
          <div className="col-span-4 flex flex-col items-center justify-center">
            <img src={cryingDaramgi} alt="Crying Daramgi" className="w-16 mt-12 mb-4" />
            <p className="text-center text-gray-500">
              문서가 없습니다.
            </p>
          </div>
      ) : (
        documents.map((doc) => (
          <DocumentItem
            key={`${doc.type}-${doc.documentId}`}
            doc={doc}
            handleDocumentClick={handleDocumentClick}
          />
        ))
      )}
    </div>
  )
}
