import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LlmService {

  private apiUrl = 'http://localhost:8081/llm';

  constructor(private http: HttpClient) { }


  suggestUseCases(userDescription: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/suggest-use-cases`, { userDescription });
  }



  submitSelectedUseCases(selectedUseCases:string): void {
    // Send selected use cases to the backend
    this.http.post(`${this.apiUrl}/select-use-cases`, {selectedUseCases}).subscribe((response) => {
      console.log('Submitted successfully:', response);
    });
    console.log(selectedUseCases);

  }

  submitArchitecture(architecture: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/select-architecture`, { architecture });
  }

  submitTechnologies(selectedTechnologies: string ): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/select-technologies`, { selectedTechnologies });
  }


  downloadFile(): void{
    this.http.get(`${this.apiUrl}/generate-zip`,{responseType: 'blob' as 'json'}).subscribe(
        (response: any) =>{
            let dataType = response.type;
            let binaryData = [];
            binaryData.push(response);
            let downloadLink = document.createElement('a');
            downloadLink.href = window.URL.createObjectURL(new Blob(binaryData, {type: dataType}));
            document.body.appendChild(downloadLink);
            downloadLink.click();
        }
    )
}


}
