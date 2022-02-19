import {User} from './User';
import {Rent} from './Rent';

export class Project {
    id: number;
    name: string;
    prodManager: User;
    startDate: Date;
    expEndDate: Date;
    rents: Rent[];

    static toJsonString(project: Project): string {
        const projectJson = JSON.parse(JSON.stringify(project));

        projectJson.rents = [];
        project.rents.forEach(rent => {
            projectJson.rents.push(Rent.toJsonWithoutRoot(rent))
        })

        return `{\"Project\": ${JSON.stringify(projectJson)}}`;
    }

    static fromJson(project: Project): Project {
        const newProject = new Project();
        newProject.id = project.id;
        newProject.prodManager = project.prodManager;
        newProject.name = project.name;
        newProject.startDate = project.startDate;
        newProject.expEndDate = project.expEndDate;

        project.rents.forEach(rent => {
            newProject.rents.push(Rent.fromJson(rent));
        });

        return newProject;
    }

    constructor(id: number = -1, name: string = '', prodManager: User = null,
                startDate: Date = null, expEndDate: Date = null, rents: Rent[] = []) {
        this.id = id;
        this.name = name;
        this.prodManager = prodManager;
        this.startDate = startDate;
        this.expEndDate = expEndDate;
        this.rents = rents;
    }
}
