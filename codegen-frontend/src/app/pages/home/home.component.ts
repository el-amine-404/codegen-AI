import { Component, OnInit } from '@angular/core';
import { LlmService } from '../../core/services/api/llm.service';
import { FormArray, FormBuilder, FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { UseCase } from '../../core/model/usecase.entity';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [ ReactiveFormsModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent{

  suggestedUseCases: UseCase[] = [];
  descriptionForm: FormGroup;
  useCaseForm: FormGroup;
  architectureForm: FormGroup;
  technologyForm: FormGroup;


  technologiesAndTools = {
    backend: [
      { name: 'Spring Boot', description: 'A Java-based framework for backend development.' },
      { name: 'Node.js', description: 'A JavaScript runtime for building scalable network applications.' },
    ],
    frontend: [
      { name: 'Angular', description: 'A popular TypeScript-based frontend framework.' },
      { name: 'React', description: 'A JavaScript library for building user interfaces.' },
    ],
    tools: [
      { name: 'Docker', description: 'A tool to containerize applications.' },
      { name: 'Git', description: 'A distributed version control system.' },
    ],
  };


  constructor(private fb: FormBuilder, private llmService: LlmService){

    this.descriptionForm = this.fb.group({
      userDescription: [''],
    });

    this.useCaseForm = this.fb.group({
      selectedUseCases: this.fb.array([])
    });

    this.architectureForm = this.fb.group({
      selectedArchitecture: [''],
    });

    this.technologyForm = this.fb.group({
      backend: [''],
      frontend: [''],
      tools: [''],
    });
  }


   // Getter for the selectedUseCases FormArray
  get selectedUseCases(): FormArray {
    return this.useCaseForm.get('selectedUseCases') as FormArray;
  }

  onCheckboxChange(event: any, useCase: UseCase): void {
    const selectedUseCases = this.useCaseForm.get('selectedUseCases') as FormArray;

    if (event.target.checked) {
      // Add the selected use case
      selectedUseCases.push(this.fb.control(useCase.name));
    } else {
      // Remove the unselected use case
      const index = selectedUseCases.controls.findIndex(control => control.value.name === useCase.name);
      if (index !== -1) {
        selectedUseCases.removeAt(index);
      }
    }
  }

  onSubmit() {
    const userDescription = this.descriptionForm.value.userDescription;
    this.llmService.suggestUseCases(userDescription).subscribe({
      next: response => {
        this.suggestedUseCases = response;
        console.log(response);
      }, error: error => {
        console.error('Error fetching use cases:', error);
      }
    });
  }


  submitSelectedUseCases(): void {
    // Send selected use cases to the backend
    const selectedUseCases = this.useCaseForm.value.selectedUseCases;
    this.llmService.submitSelectedUseCases(selectedUseCases.join(', '));
    console.log('Selected Use Cases:', selectedUseCases.join(', '));
  }



  onArchitectureSubmit(): void {
    const selectedArchitecture = this.architectureForm.value.selectedArchitecture;
    if (!selectedArchitecture) {
      alert('Please select an architecture.');
      return;
    }

    this.llmService.submitArchitecture(selectedArchitecture).subscribe({
      next: () => {
        alert('Architecture submitted successfully!');
      },
      error: (error) => {
        console.error('Error submitting architecture:', error);
      },
    });
  }


  submitTechnologies(): void {
    const backend = this.technologyForm.value.backend;
    const frontend = this.technologyForm.value.frontend;
    const tools = this.technologyForm.value.tools;
    const selectedTechnologies = backend + ","+ frontend + "," + tools;
    console.log("selected techs are: " + selectedTechnologies);

    this.llmService.submitTechnologies(selectedTechnologies).subscribe({
      next: () => {
        alert('Selected technologies submitted successfully!');
      },
      error: (error) => {
        console.error('Error submitting technologies:', error);
      },
    });
  }

  downloadZip(){
    this.llmService.downloadFile();
  }

}
